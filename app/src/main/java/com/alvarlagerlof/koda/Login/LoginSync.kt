package com.alvarlagerlof.koda.Login

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.RequestQueue.RequestQueueSync
import com.alvarlagerlof.koda.Vars
import com.arasthel.asyncjob.AsyncJob
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.JavaNetCookieJar
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy


/**
 * Created by alvar on 2016-10-15.
 */

class LoginSync(private val context: Context)  {


    init {

        AsyncJob.doInBackground {

            if (PersistentCookieStore(context).cookies.size < 1 || PreferenceManager.getDefaultSharedPreferences(context).getString(Vars.PREF_EMAIL, null) == null) {
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }

            if (context.isConnected()) {

                val cookieHandler = CookieManager(PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)
                val client = OkHttpClient.Builder().cookieJar(JavaNetCookieJar(cookieHandler)).build()

                val formBody = FormBody.Builder()
                        .add("email", PreferenceManager.getDefaultSharedPreferences(context).getString(Vars.PREF_EMAIL, "")!!)
                        .add("password", PreferenceManager.getDefaultSharedPreferences(context).getString(Vars.PREF_EMAIL, "")!!)
                        .add("headless", "thisIsHeadLess")
                        .build()

                val request = Request.Builder()
                        .url(Vars.URL_LOGIN)
                        .post(formBody)
                        .build()

                try {
                    val response = client.newCall(request).execute()
                    response.body()?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }



            if (PreferenceManager.getDefaultSharedPreferences(context).getString(Vars.PREF_EMAIL, null) != null) {
                RequestQueueSync(context)
            }

        }
    }

}

