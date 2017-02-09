package com.alvarlagerlof.koda.Guides;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.Guides.Image.GuidesImageObject;
import com.alvarlagerlof.koda.Guides.Line.GuidesLineObject;
import com.alvarlagerlof.koda.Guides.SmallImageWithButton.GuidesSmallImageWithButtonObject;
import com.alvarlagerlof.koda.Guides.Text.GuidesTextObject;
import com.alvarlagerlof.koda.Guides.TextWithTitle.GuidesTextWithTitleObject;
import com.alvarlagerlof.koda.Guides.YoutubeList.GuidesYouTubeListObject;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.Universal.UniversalLoadingObject;
import com.alvarlagerlof.koda.Universal.UniversalOfflineObject;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.google.firebase.crash.FirebaseCrash;

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

class GuidesGetData extends AsyncTask<Void, Void, String> {

    private Context context;
    private ArrayList<Object> list;
    private RecyclerView.Adapter adapter;

    GuidesGetData(Context context, ArrayList<Object> list, RecyclerView.Adapter adapter) {
        this.context = context;
        this.list = list;
        this.adapter = adapter;
    }




    // Code here
    @Override
    final protected void onPreExecute() {

        list.add(new UniversalLoadingObject());
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
                        .url(PrefValues.URL_GUIDES)
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

                // TODO: FOR LOOP OVER WHOLE LIST AND BASE64 DECODE


                JSONObject jsonObject = new JSONObject(json);
                JSONArray items = jsonObject.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {

                    JSONObject item = items.getJSONObject(i);

                    switch (item.getString("type")) {
                        case "line":
                            list.add(new GuidesLineObject());
                            break;

                        case "text":
                            list.add(new GuidesTextObject(item.getJSONObject("content").getString("text")));
                            break;

                        case "textWithTitle":
                            list.add(new GuidesTextWithTitleObject(item.getJSONObject("content").getString("title"),
                                                                   item.getJSONObject("content").getString("text")));
                            break;

                        case "image":
                            list.add(new GuidesImageObject(item.getJSONObject("content").getString("imageUrl"),
                                                           item.getJSONObject("content").getString("link")));
                            break;

                        case "smallImageWithButton":
                            list.add(new GuidesSmallImageWithButtonObject(item.getJSONObject("content").getString("imageUrl"),
                                                                          item.getJSONObject("content").getString("text"),
                                                                          item.getJSONObject("content").getString("link")));

                            break;

                        case "youtubeList":
                            list.add(new GuidesYouTubeListObject(item.getJSONObject("content").getJSONArray("videos")));
                            break;

                    }

                }

                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
                FirebaseCrash.report(e);
            }

        } else {

            list.clear();
            list.add(new UniversalOfflineObject());
            adapter.notifyDataSetChanged();

        }
        
    }
}