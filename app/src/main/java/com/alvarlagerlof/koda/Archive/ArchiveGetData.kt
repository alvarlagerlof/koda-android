package com.alvarlagerlof.koda.Archive

import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Extensions.base64Decode
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Vars
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

internal class ArchiveGetData(private val context: Context, private val list: ArrayList<ArchiveObject>, private val adapter: RecyclerView.Adapter<*>) : AsyncTask<Void, Void, String>() {


    // Code here
    override fun onPreExecute() {

        list.add(ArchiveObject("", "", "", "", "", "", "", "", false, ArchiveAdapter.TYPE_LOADING))
        adapter.notifyDataSetChanged()

    }

    override fun doInBackground(vararg progress: Void): String? {
        if (context.isConnected()) {

            try {

                val cookieHandler = CookieManager(
                        PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL)

                val client = OkHttpClient.Builder()
                        .cookieJar(JavaNetCookieJar(cookieHandler))
                        .build()

                val request = Request.Builder()
                        .url(Vars.URL_ARCHIVE)
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

        if (json != null && context.isConnected()) {

            list.clear()

            try {
                val jsonArray = JSONArray(json)
                for (i in 0..jsonArray.length() - 1) {
                    try {
                        val jsonObject = jsonArray.getJSONObject(i)

                        var title = jsonObject.getString("title").base64Decode()
                        var author = jsonObject.getString("author").base64Decode()
                        var description = jsonObject.getString("description").base64Decode()
                        val updated = jsonObject.getString("updated")
                        val publicID = jsonObject.getString("publicID")
                        val likesCount = jsonObject.getString("likes")
                        val commentCount = (Random().nextInt(100) + 1).toString()
                        val charCount = (Random().nextInt(400) + 1).toString()
                        val liked = jsonObject.getString("liked") == "true"

                        if (title == "") title = context.getString(R.string.unnamed)
                        if (author == "") author = context.getString(R.string.anonymous)
                        if (description == "") description = context.getString(R.string.no_description)

                        list.add(ArchiveObject(publicID, title, author, description, updated, likesCount, commentCount, charCount, liked, ArchiveAdapter.TYPE_ITEM))

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            adapter.notifyDataSetChanged()

        } else {

            list.clear()
            list.add(ArchiveObject("", "", "", "", "", "", "", "", false, ArchiveAdapter.TYPE_OFFLINE))
            adapter.notifyDataSetChanged()
        }

    }
}