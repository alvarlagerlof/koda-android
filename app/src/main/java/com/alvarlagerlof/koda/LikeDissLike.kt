package com.alvarlagerlof.koda

import android.content.Context
import android.os.AsyncTask
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
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

class LikeDissLike(private val context: Context, private val url: String, private val publicID: String) : AsyncTask<Void, Void, String>() {


    override fun doInBackground(vararg params: Void): String? {

        val cookieHandler = CookieManager(
                PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)

        val client = OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(cookieHandler))
                .build()

        val formBody = FormBody.Builder()
                .add("headless", "thisIsHeadless")
                .build()

        val request = Request.Builder()
                .url(url + publicID)
                .post(formBody)
                .build()

        try {
            val response = client.newCall(request).execute()
            response.body()!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}

