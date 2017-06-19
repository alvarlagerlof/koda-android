package com.alvarlagerlof.koda.Settings

import android.content.Context
import android.os.AsyncTask
import android.preference.PreferenceManager
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Vars
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.JavaNetCookieJar
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy

/**
 * Created by alvar on 2016-11-09.
 */

internal class SettingsUpdateNick(var context: Context, var nick: String) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void): Void? {

        // Save locally
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(Vars.PREF_NICK, nick)
                .apply()


        // Save on the server
        if (context.isConnected()) {
            val cookieHandler = CookieManager(
                    PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)

            val client = OkHttpClient.Builder()
                    .cookieJar(JavaNetCookieJar(cookieHandler))
                    .build()

            val data = FormBody.Builder()
                    .add("nick", nick)
                    .build()

            val request = Request.Builder()
                    .url(Vars.URL_SETTINGS_SAVE_NICK)
                    .post(data)
                    .build()

            try {
                val response = client.newCall(request).execute()
                response.body()!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }


        return null
    }
}