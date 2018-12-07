package com.alvarlagerlof.koda.Profile

import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Extensions.timeStampToDate
import com.alvarlagerlof.koda.Vars
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.JavaNetCookieJar
import org.json.JSONException
import org.json.JSONObject
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*

/**
 * Created by alvar on 2016-11-08.
 */

internal class ProfileGetData : AsyncTask<Void, Void, String>() {

    private var context: Context? = null
    private var list: ArrayList<ProfileObject>? = null
    private var adapter: RecyclerView.Adapter<*>? = null

    fun setParams(context: Context, list: ArrayList<ProfileObject>, adapter: RecyclerView.Adapter<*>) {
        this.context = context
        this.list = list
        this.adapter = adapter
    }


    // Code here
    override fun onPreExecute() {

        list!!.add(ProfileObject("", "", "", "", "", "", "", "", false, "", ProfileAdapter.TYPE_LOADING))
        adapter!!.notifyDataSetChanged()

    }

    override fun doInBackground(vararg progress: Void): String? {
        if (context!!.isConnected()) {
            try {

                val cookieHandler = CookieManager(
                        PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)

                val client = OkHttpClient.Builder()
                        .cookieJar(JavaNetCookieJar(cookieHandler))
                        .build()

                val request = Request.Builder()
                        .url(Vars.URL_PROFILE)
                        .build()

                val response = client.newCall(request).execute()
                val json = response.body()!!.string()
                response.body()!!.close()

                return json

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        return null
    }


    override fun onPostExecute(json: String?) {

        if (json != null && context!!.isConnected()) {

            list!!.clear()

            try {

                val jsonObject = JSONObject(json)

                val authorTitle = jsonObject.getJSONObject("profile").getString("author")
                val numberOfProjects = jsonObject.getJSONObject("profile").getString("numberOfProjects")

                list!!.add(ProfileObject("", "", authorTitle, "", "", "", "", "", false, numberOfProjects, ProfileAdapter.TYPE_HEADER))

                val projectsJsonArray = jsonObject.getJSONArray("projects")

                for (i in 0..projectsJsonArray.length() - 1) {

                    val projectsJsonObject = projectsJsonArray.getJSONObject(i)

                    val title = projectsJsonObject.getString("title")
                    val description = projectsJsonObject.getString("description")
                    val updated = "Uppdaterad " + projectsJsonObject.getString("updated").timeStampToDate()
                    val publicID = projectsJsonObject.getString("publicID")
                    val like_count = projectsJsonObject.getString("like_count")
                    val comment_count = projectsJsonObject.getString("comment_count")
                    val char_count = projectsJsonObject.getString("char_count")

                    list!!.add(ProfileObject(publicID, title, updated, description, updated, like_count, comment_count, char_count, false, "", ProfileAdapter.TYPE_ITEM))

                }

                adapter!!.notifyDataSetChanged()

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else {

            list!!.clear()
            list!!.add(ProfileObject("", "", "", "", "", "", "", "", false, "", ProfileAdapter.TYPE_OFFLINE))
            adapter!!.notifyDataSetChanged()

        }

    }
}