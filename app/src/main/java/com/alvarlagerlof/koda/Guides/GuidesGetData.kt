package com.alvarlagerlof.koda.Guides

import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Guides.Image.GuidesImageObject
import com.alvarlagerlof.koda.Guides.Line.GuidesLineObject
import com.alvarlagerlof.koda.Guides.SmallImageWithButton.GuidesSmallImageWithButtonObject
import com.alvarlagerlof.koda.Guides.Text.GuidesTextObject
import com.alvarlagerlof.koda.Guides.TextWithTitle.GuidesTextWithTitleObject
import com.alvarlagerlof.koda.Guides.YoutubeList.GuidesYouTubeListObject
import com.alvarlagerlof.koda.Universal.UniversalLoadingObject
import com.alvarlagerlof.koda.Universal.UniversalOfflineObject
import com.alvarlagerlof.koda.Vars
import com.google.firebase.crash.FirebaseCrash
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

internal class GuidesGetData(private val context: Context, private val list: ArrayList<Any>, private val adapter: RecyclerView.Adapter<*>) : AsyncTask<Void, Void, String>() {


    // Code here
    override fun onPreExecute() {

        list.add(UniversalLoadingObject())
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
                        .url(Vars.URL_GUIDES)
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

                // TODO: FOR LOOP OVER WHOLE LIST AND BASE64 DECODE


                val jsonObject = JSONObject(json)
                val items = jsonObject.getJSONArray("items")

                for (i in 0..items.length() - 1) {

                    val item = items.getJSONObject(i)

                    when (item.getString("type")) {
                        "line" -> list.add(GuidesLineObject())

                        "text" -> list.add(GuidesTextObject(item.getJSONObject("content").getString("text")))

                        "textWithTitle" -> list.add(GuidesTextWithTitleObject(item.getJSONObject("content").getString("title"),
                                item.getJSONObject("content").getString("text")))

                        "image" -> list.add(GuidesImageObject(item.getJSONObject("content").getString("imageUrl"),
                                item.getJSONObject("content").getString("link")))

                        "smallImageWithButton" -> list.add(GuidesSmallImageWithButtonObject(item.getJSONObject("content").getString("imageUrl"),
                                item.getJSONObject("content").getString("text"),
                                item.getJSONObject("content").getString("link")))

                        "youtubeList" -> list.add(GuidesYouTubeListObject(item.getJSONObject("content").getJSONArray("videos")))
                    }

                }

                adapter.notifyDataSetChanged()

            } catch (e: JSONException) {
                e.printStackTrace()
                FirebaseCrash.report(e)
            }

        } else {

            list.clear()
            list.add(UniversalOfflineObject())
            adapter.notifyDataSetChanged()

        }

    }
}