package com.alvarlagerlof.koda.Comments;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.RemoteConfigValues;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Utils.Base64Utils;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.alvarlagerlof.koda.Utils.DateConversionUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-11-08.
 */

class CommentsGetData extends AsyncTask<Void, Void, String> {

    private Context context;
    private String publicID;
    private ArrayList<CommentsObject> list;
    private RecyclerView.Adapter adapter;

    CommentsGetData(Context context, String publicID, ArrayList<CommentsObject> list, RecyclerView.Adapter adapter) {
        this.context = context;
        this.publicID = publicID;
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

                FormBody formBody = new FormBody.Builder()
                        .add("order", "DESC")
                        .build();

                Request request = new Request.Builder()
                        .url(RemoteConfigValues.URL_COMMENTS + publicID)
                        .post(formBody)
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

                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String author  = Base64Utils.decode(jsonObject.getString("author"));
                            String date    = DateConversionUtils.convert(jsonObject.getString("created").equals("") ? context.getString(R.string.anonymous) : jsonObject.getString("created"));
                            String comment = Base64Utils.decode(jsonObject.getString("comment"));

                            if (author.equals("")) {
                                author = context.getString(R.string.anonymous);
                            }

                            list.add(new CommentsObject(author, date, comment, CommentsAdapter.TYPE_ITEM));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    list.add(new CommentsObject("", "", "", CommentsAdapter.TYPE_NO_COMMENTS));
                }

                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }

        } else {

            list.clear();
            list.add(new CommentsObject("", "", "", CommentsAdapter.TYPE_OFFLINE));
            adapter.notifyDataSetChanged();

        }
        
    }
}