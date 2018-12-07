package com.alvarlagerlof.koda.Api

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Universal.UniversalLoadingObject
import com.alvarlagerlof.koda.Universal.UniversalOfflineObject
import com.alvarlagerlof.koda.Vars
import com.arasthel.asyncjob.AsyncJob
import com.google.firebase.analytics.FirebaseAnalytics
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.JavaNetCookieJar
import org.json.JSONArray
import org.json.JSONException
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*

/**
 * Created by alvar on 2016-11-08.
 */

class ApiGetData(private val context: Context, private val url: String, private val list: ArrayList<Any>, private val adapter: RecyclerView.Adapter<*>) {

    init {
        list.add(UniversalLoadingObject())
        adapter.notifyDataSetChanged()

        AsyncJob.doInBackground {
            if (context.isConnected()) {
                try {

                    val cookieHandler = CookieManager(PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)
                    val client = OkHttpClient.Builder().cookieJar(JavaNetCookieJar(cookieHandler)).build()

                    val request = Request.Builder().url(url).build()

                    val response = client.newCall(request).execute()
                    val json = response.body()!!.string()
                    response.body()!!.close()

                    if (json != null) {
                        try {
                            list.clear()

                            val jsonArray = JSONArray(json)

                            for (i in 0..jsonArray.length() - 1) {
                                try {
                                    val jsonobject = jsonArray.getJSONObject(i)
                                    val command = jsonobject.getString("command")
                                    val description = jsonobject.getString("description")

                                    list.add(ApiObject(command, description))
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                            }

                            AsyncJob.doOnMainThread { adapter.notifyDataSetChanged() }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        list.clear()
                        list.add(UniversalOfflineObject())
                        AsyncJob.doOnMainThread { adapter.notifyDataSetChanged() }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                list.clear()
                list.add(UniversalOfflineObject())
                AsyncJob.doOnMainThread { adapter.notifyDataSetChanged() }

                when (url) {
                    Vars.URL_API_2D -> FirebaseAnalytics.getInstance(context).logEvent("api_tab_2d", Bundle())
                    Vars.URL_API_3D -> FirebaseAnalytics.getInstance(context).logEvent("api_tab_3d", Bundle())
                }

            }
        }
    }
}