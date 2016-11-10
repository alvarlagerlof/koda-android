package com.alvarlagerlof.koda.Comments;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-11-08.
 */

class CommentsGetData extends AsyncTask<Void, Void, String> {

    private Context context;
    private ArrayList<CommentsObject> list;
    private RecyclerView.Adapter adapter;

    CommentsGetData(Context context, ArrayList<CommentsObject> list, RecyclerView.Adapter adapter) {
        this.context = context;
        this.list = list;
        this.adapter = adapter;
    }




    // Code here
    @Override
    final protected void onPreExecute() {

        list.add(new CommentsObject("", "", "", CommentsAdapter.TYPE_HEADER));
        list.add(new CommentsObject("", "", "", CommentsAdapter.TYPE_LOADING));
        adapter.notifyDataSetChanged();

    }

    @Override
    final protected String doInBackground(Void... progress) {
        if (ConnectionUtils.isConnected(context)) {
            try {

                CookieHandler cookieHandler = new CookieManager(
                        new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);

                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(new JavaNetCookieJar(cookieHandler))
                        .build();

                Request request = new Request.Builder()
                        .url(PrefValues.URL_COMMENTS)
                        .build();

                Response response = client.newCall(request).execute();
                String json = response.body().string();
                response.body().close();

                Log.d("json", json);

                return json;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    @Override
    final protected void onPostExecute(String json) {

        if (json != null && ConnectionUtils.isConnected(context)) {

            list.clear();
            list.add(new CommentsObject("", "", "", CommentsAdapter.TYPE_HEADER));


            try {

                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String by      = jsonObject.getString("author");
                        String date    = jsonObject.getString("date").equals("") ? context.getString(R.string.anonymous) : jsonObject.getString("date");
                        String comment = jsonObject.getString("comment");

                        list.add(new CommentsObject(by, date, comment, CommentsAdapter.TYPE_ITEM));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

            list.clear();
            list.add(new CommentsObject("", "", "", CommentsAdapter.TYPE_OFFLINE));
            adapter.notifyDataSetChanged();

        }
        
    }
}