package com.alvarlagerlof.koda.Comments

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Vars
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.JavaNetCookieJar
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy

/**
 * Created by alvar on 2017-01-28.
 */

internal class CommentsSend(private val context: Context, publicID: String, private val comment: String) : AsyncTask<Void, Void, String>() {
    private val url: String = Vars.URL_COMMENTS_SEND + publicID


    override fun doInBackground(vararg params: Void): String? {

        Log.d("URL", url)


        val cookieHandler = CookieManager(
                PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)

        val client = OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(cookieHandler))
                .build()

        val formBody = FormBody.Builder()
                .add("comment", comment)
                .build()

        val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()


        try {
            val response = client.newCall(request).execute()
            Log.d("RESPONSE", response.body()!!.string())
            Toast.makeText(context, response.body()!!.string(), Toast.LENGTH_LONG).show()


            response.body()!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}

