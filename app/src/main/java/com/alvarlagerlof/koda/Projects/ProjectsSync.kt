package com.alvarlagerlof.koda.Projects

import android.content.Context
import android.content.Intent
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Editor.EditorActivity
import com.alvarlagerlof.koda.Extensions.base64Decode
import com.alvarlagerlof.koda.Extensions.base64Encode
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Vars
import com.arasthel.asyncjob.AsyncJob
import io.realm.Realm
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.JavaNetCookieJar
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy

/**
 * Created by alvar on 2016-11-08.
 */

class ProjectsSync {

    private var context: Context? = null
    private var openPrivateID: String? = null


    constructor(context: Context) {
        this.context = context

        if (context.isConnected()) {
            sendToServer()
        } else {
            EventBus.getDefault().post(ProjectsSyncEvent(message = "offline"))
        }

    }


    constructor(context: Context, openPrivateID: String) {
        this.context = context
        this.openPrivateID = openPrivateID

        if (context.isConnected()) {
            val intent = Intent(context, EditorActivity::class.java)
            intent.putExtra("privateID", openPrivateID)
            context.startActivity(intent)
            sendToServer()

        } else {
            EventBus.getDefault().post(ProjectsSyncEvent(message = "offline"))
            val intent = Intent(context, EditorActivity::class.java)
            intent.putExtra("privateID", openPrivateID)
            context.startActivity(intent)
        }


    }


    private fun sendToServer() {

        AsyncJob.doInBackground {
            try {

                // Get all Realm projects
                val realmProjects = Realm.getDefaultInstance().where(ProjectsRealmObject::class.java).findAll()


                // Create a json array
                val projects = JSONArray()


                // Loop over Realm projects
                for (realmObject in realmProjects) {

                    val jsonObject = JSONObject()
                            .put("privateID", realmObject.privateID)
                            .put("updated", realmObject.updatedServer)

                    if (!realmObject.synced) {
                        jsonObject.put("title", realmObject.title.base64Encode())
                        jsonObject.put("description", realmObject.description.base64Encode())
                        jsonObject.put("public", realmObject.isPublic)
                        jsonObject.put("code", realmObject.code.base64Encode())
                    }

                    projects.put(jsonObject)

                }


                // Send it
                val request = Request.Builder()
                        .url(Vars.URL_PROJECTS_SYNC)
                        .post(FormBody.Builder()
                                .add("projects", projects.toString())
                                .build())
                        .build()

                val response = OkHttpClient.Builder().cookieJar(JavaNetCookieJar(CookieManager(PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL))).build().newCall(request).execute()

                if (response.isSuccessful) {

                    val responseString = response.body()?.string()
                    if (responseString != null && openPrivateID != null) {

                        val json = JSONObject(responseString)

                        (0..json.getJSONArray("projects").length() - 1)
                                .asSequence()
                                .map {
                                    json.getJSONArray("projects").getJSONObject(it)
                                }
                                .filter { openPrivateID == it.getString("old_privateID") }
                                .forEach { openPrivateID = it.getString("privateID") }
                    }

                    AsyncJob.doOnMainThread {
                        sendNothing()
                    }
                }

                response.body()!!.close()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


    }



    private fun sendNothing() {
        AsyncJob.doInBackground {
            try {

                // Send it
                val request = Request.Builder()
                        .url(Vars.URL_PROJECTS_SYNC)
                        .post(FormBody.Builder()
                                .add("projects", "[]")
                                .build())
                        .build()

                val response = OkHttpClient.Builder().cookieJar(JavaNetCookieJar(CookieManager(PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL))).build().newCall(request).execute()


                // If success
                if (response.isSuccessful) {
                    val json = JSONObject(response.body()!!.string())
                    AsyncJob.doOnMainThread { dealWithRespose(json) }
                }


                // Close response
                response.body()!!.close()


            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun dealWithRespose(response: JSONObject?) {


        AsyncJob.doInBackground {
            try {

                val realm = Realm.getDefaultInstance()


                // If response not empty
                if (response != null && response.getJSONArray("projects").length() > 0) {


                    // Remove projects that exists on the client but not on the server
                    realm.where(ProjectsRealmObject::class.java).findAll().forEach {
                        var hasFoundInJson = false
                        for (i in 0..response.getJSONArray("projects").length() - 1) {
                            if (response.getJSONArray("projects").getJSONObject(i).getString("privateID") == it.privateID) {
                                hasFoundInJson = true
                            }
                        }
                        if (!hasFoundInJson) {
                            realm.beginTransaction()
                            it.deleteFromRealm()
                            realm.commitTransaction()
                        }
                    }


                    // Loop over response
                    for (i in 0..response.getJSONArray("projects").length() - 1) {


                        // Get project
                        val responseItem = response.getJSONArray("projects").getJSONObject(i)

                        // Get Realm project based on privateID
                        val realmProject = Realm.getDefaultInstance().where(ProjectsRealmObject::class.java).equalTo("privateID", responseItem.getString("old_privateID")).findFirst()


                        if (realmProject != null) {

                            // Edit the existing one
                            realm.beginTransaction()
                            realmProject.privateID = responseItem.getString("privateID")
                            realmProject.publicID = responseItem.getString("publicID")
                            realmProject.title = responseItem.getString("title").base64Decode()
                            realmProject.description = responseItem.getString("description").base64Decode()
                            realmProject.code = responseItem.getString("code").base64Decode()
                            realmProject.synced = true
                            realmProject.isPublic = responseItem.getBoolean("public")
                            realmProject.updatedServer = responseItem.getString("updated")
                            realmProject.updatedRealm = responseItem.getString("updated")
                            realm.commitTransaction()

                        } else {

                            // Create a new one
                            realm.beginTransaction()
                            val realmObject = realm.createObject(ProjectsRealmObject::class.java) // Create a new object
                            realmObject.privateID = responseItem.getString("privateID")
                            realmObject.publicID = responseItem.getString("publicID")
                            realmObject.title = responseItem.getString("title").base64Decode()
                            realmObject.description = responseItem.getString("description").base64Decode()
                            realmObject.code = responseItem.getString("code").base64Decode()
                            realmObject.synced = true
                            realmObject.isPublic = responseItem.getBoolean("public")
                            realmObject.updatedServer = responseItem.getString("updated")
                            realmObject.updatedRealm = responseItem.getString("updated")
                            realm.commitTransaction()

                        }
                    }


                } else {
                    EventBus.getDefault().post(ProjectsSyncEvent(message = "offline"))
                }

                if (openPrivateID != null)  {
                    EventBus.getDefault().post(ProjectsSyncEvent(message = "synced_id_" + openPrivateID))
                }


            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }



    }




}