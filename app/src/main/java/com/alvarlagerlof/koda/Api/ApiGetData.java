package com.alvarlagerlof.koda.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.alvarlagerlof.koda.ConnectionUtils;
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;

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

class ApiGetData extends AsyncTask<Void, Void, String> {

    private Context context;
    private String url;
    private ArrayList<ApiObject> list;
    private RecyclerView.Adapter adapter;

    ApiGetData(Context context, String url, ArrayList<ApiObject> list, RecyclerView.Adapter adapter) {
        this.context = context;
        this.url = url;
        this.list = list;
        this.adapter = adapter;
    }




    // Code here
    @Override
    final protected void onPreExecute() {

        list.add(new ApiObject("Loading", ""));
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
                        .url(url)
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

        if (json != null) {

            try {
                list.remove(0);

                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String command = jsonobject.getString("command");
                        String description = jsonobject.getString("description");
                        list.add(new ApiObject(command, description));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}