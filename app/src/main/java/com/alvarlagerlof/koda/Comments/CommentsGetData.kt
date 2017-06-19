package com.alvarlagerlof.koda.Comments

import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Extensions.base64Decode
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Extensions.timeStampToDate
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Vars
import com.google.firebase.crash.FirebaseCrash
import okhttp3.FormBody
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

internal class CommentsGetData(private val context: Context, private val publicID: String, private val list: ArrayList<CommentsObject>, private val adapter: RecyclerView.Adapter<*>) : AsyncTask<Void, Void, String>() {


    // Code here
    override fun onPreExecute() {

        list.add(CommentsObject("", "", "", CommentsAdapter.TYPE_HEADER))
        list.add(CommentsObject("", "", "", CommentsAdapter.TYPE_LOADING))
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

                val formBody = FormBody.Builder()
                        .add("order", "DESC")
                        .build()

                val request = Request.Builder()
                        .url(Vars.URL_COMMENTS + publicID)
                        .post(formBody)
                        .build()

                val response = client.newCall(request).execute()
                val json = response.body()!!.string()
                response.body()!!.close()

                Log.d("json", json)

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
            list.add(CommentsObject("", "", "", CommentsAdapter.TYPE_HEADER))

            try {

                val jsonArray = JSONArray(json)

                if (jsonArray.length() > 0) {
                    for (i in 0..jsonArray.length() - 1) {
                        try {
                            val jsonObject = jsonArray.getJSONObject(i)

                            var author = jsonObject.getString("author").base64Decode()
                            val date = jsonObject.getString("created").timeStampToDate()
                            val comment = jsonObject.getString("comment").base64Decode()

                            if (author == "") {
                                author = context.getString(R.string.anonymous)
                            }

                            list.add(CommentsObject(author, date, comment, CommentsAdapter.TYPE_ITEM))

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    list.add(CommentsObject("", "", "", CommentsAdapter.TYPE_NO_COMMENTS))
                }

                adapter.notifyDataSetChanged()

            } catch (e: JSONException) {
                e.printStackTrace()
                FirebaseCrash.report(e)
            }

        } else {

            list.clear()
            list.add(CommentsObject("", "", "", CommentsAdapter.TYPE_OFFLINE))
            adapter.notifyDataSetChanged()

        }

    }
}