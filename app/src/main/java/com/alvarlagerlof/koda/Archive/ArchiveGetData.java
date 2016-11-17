package com.alvarlagerlof.koda.Archive;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Utils.Base64Utils;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-11-08.
 */

class ArchiveGetData extends AsyncTask<Void, Void, String> {

    private Context context;
    private ArrayList<ArchiveObject> list;
    private RecyclerView.Adapter adapter;

    ArchiveGetData(Context context, ArrayList<ArchiveObject> list, RecyclerView.Adapter adapter) {
        this.context = context;
        this.list = list;
        this.adapter = adapter;
    }


    // Code here
    @Override
    final protected void onPreExecute() {

        list.add(new ArchiveObject("", "", "", "", "", "", "", "", false, ArchiveAdapter.TYPE_HEADER));
        list.add(new ArchiveObject("", "", "", "", "", "", "", "", false, ArchiveAdapter.TYPE_LOADING));
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
                        .url(PrefValues.URL_ARCHIVE)
                        .build();

                Response response = client.newCall(request).execute();
                String json = response.body().string();
                response.body().close();

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
            list.add(new ArchiveObject("", "", "", "", "", "", "", "", false, ArchiveAdapter.TYPE_HEADER));

            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String title        = Base64Utils.decode(jsonObject.getString("title"));
                        String author       = Base64Utils.decode(jsonObject.getString("author"));
                        String description  = Base64Utils.decode(jsonObject.getString("description"));
                        String updated      = jsonObject.getString("updated");
                        String publicID     = jsonObject.getString("publicID");
                        String likesCount   = jsonObject.getString("likes");
                        String commentCount = String.valueOf(new Random().nextInt(100) + 1);
                        String charCount    = String.valueOf(new Random().nextInt(400) + 1);
                        Boolean liked       = false;

                        if (title.equals("")) title = context.getString(R.string.unnamed);
                        if (author.equals("")) author = context.getString(R.string.anonymous);
                        if (description.equals("")) description = context.getString(R.string.no_description);

                        list.add(new ArchiveObject(publicID, title, author, description, updated, likesCount, commentCount, charCount, liked, ArchiveAdapter.TYPE_ITEM));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            adapter.notifyDataSetChanged();

        } else {

            list.clear();
            list.add(new ArchiveObject("", "", "", "", "", "", "", "", false, ArchiveAdapter.TYPE_OFFLINE));
            adapter.notifyDataSetChanged();
        }
        
    }
}