package com.alvarlagerlof.koda.Profile;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.alvarlagerlof.koda.Utils.DateConversionUtils;

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

class ProfileGetData extends AsyncTask<Void, Void, String> {

    private Context context;
    private ArrayList<ProfileObject> list;
    private RecyclerView.Adapter adapter;

    final void setParams(Context context, ArrayList<ProfileObject> list, RecyclerView.Adapter adapter) {
        this.context = context;
        this.list = list;
        this.adapter = adapter;
    }




    // Code here
    @Override
    final protected void onPreExecute() {

        list.add(new ProfileObject("", "", "", "", "", "", "", "", false, "", ProfileAdapter.TYPE_LOADING));
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
                        .url(PrefValues.URL_PROFILE)
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

            try {

                JSONObject jsonObject = new JSONObject(json);

                String authorTitle      = jsonObject.getJSONObject("profile").getString("author");
                String numberOfProjects = jsonObject.getJSONObject("profile").getString("numberOfProjects");

                list.add(new ProfileObject("", "", authorTitle, "", "", "", "", "", false, numberOfProjects,  ProfileAdapter.TYPE_HEADER));

                JSONArray projectsJsonArray = jsonObject.getJSONArray("projects");

                for (int i = 0; i < projectsJsonArray.length(); i++) {

                    JSONObject projectsJsonObject = projectsJsonArray.getJSONObject(i);

                    String title          = projectsJsonObject.getString("title");
                    String description    = projectsJsonObject.getString("description");
                    String updated        = "Uppdaterad " + DateConversionUtils.convert(projectsJsonObject.getString("updated"));
                    String publicID       = projectsJsonObject.getString("publicID");
                    String like_count     = projectsJsonObject.getString("like_count");
                    String comment_count  = projectsJsonObject.getString("comment_count");
                    String char_count     = projectsJsonObject.getString("char_count");

                    list.add(new ProfileObject(publicID, title, updated, description, updated, like_count, comment_count, char_count, false, "", ProfileAdapter.TYPE_ITEM));

                }

                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

            list.clear();
            list.add(new ProfileObject("", "", "", "", "", "", "", "", false, "", ProfileAdapter.TYPE_OFFLINE));
            adapter.notifyDataSetChanged();

        }
        
    }
}