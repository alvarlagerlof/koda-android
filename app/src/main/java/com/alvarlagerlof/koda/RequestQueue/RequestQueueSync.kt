package com.alvarlagerlof.koda.RequestQueue

import android.content.Context
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Projects.ProjectsSync
import com.arasthel.asyncjob.AsyncJob
import io.realm.Realm
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.JavaNetCookieJar
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy

/**
 * Created by alvar on 2017-04-08.
 */

class RequestQueueSync(private val context: Context) {



    init {
        if (context.isConnected()) {
            eatQueue()
        } else {
            ProjectsSync(context)
        }
    }


    internal fun eatQueue() {


        AsyncJob.doInBackground {
            // Get realm
            val realm = Realm.getDefaultInstance()

            // Get all requests in queue
            val requestQueue = realm.where(RequestQueueItem::class.java).findAll()


            if (requestQueue.size == 0) {
                // If no request in queue, sync projects
                AsyncJob.doOnMainThread { ProjectsSync(context) }

            } else {
                // Else, eat queue
                val request = Request.Builder().url(requestQueue.first().url).build()

                try {
                    val response = OkHttpClient.Builder()
                            .cookieJar(JavaNetCookieJar(CookieManager(PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)))
                            .build().newCall(request).execute()

                    if (response.isSuccessful) {
                        realm.beginTransaction()
                        requestQueue.first().deleteFromRealm()
                        realm.commitTransaction()
                    } else {
                        AsyncJob.doOnMainThread { ProjectsSync(context) }
                    }

                    response.body()!!.close()


                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (requestQueue.size > 1) {
                    eatQueue()
                } else {
                    AsyncJob.doOnMainThread { ProjectsSync(context) }
                }
            }
        }
    }
}
