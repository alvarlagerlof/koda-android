package com.alvarlagerlof.koda.MyProjects;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;

import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.Utils.Base64Utils;
import com.alvarlagerlof.koda.Utils.DateConversionUtils;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-11-08.
 */

class MyProjectsGetData extends AsyncTask<Void, Void, String> {

    private Context context;
    private ArrayList<MyProjectsObject> list;
    private RecyclerView.Adapter adapter;

    MyProjectsGetData(Context context, ArrayList<MyProjectsObject> list, RecyclerView.Adapter adapter) {
        this.context = context;
        this.list = list;
        this.adapter = adapter;
    }





    // Code here
    @Override
    final protected void onPreExecute() {

        list.add(new MyProjectsObject("Loading", "", "",  "", "", false, "", "", "", ""));
        adapter.notifyDataSetChanged();

    }

    @Override
    final protected String doInBackground(Void... progress) {
        String json = null;
        if (ConnectionUtils.isConnected(context)) {
            try {

                CookieHandler cookieHandler = new CookieManager(
                        new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);

                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(new JavaNetCookieJar(cookieHandler))
                        .build();

                Request request = new Request.Builder()
                        .url(PrefValues.URL_MY_PROJECTS)
                        .build();

                Response response = client.newCall(request).execute();
                json = response.body().string();
                response.body().close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return json;
        }

        return null;
    }


    @Override
    final protected void onPostExecute(String json) {

        if (json != null) {

            list.clear();

            JSONObject jsonObject = null;
            JSONArray games = null;
            String nick = null;

            try {
                jsonObject = new JSONObject(json);

                games = jsonObject.getJSONArray("games");
                nick = Base64Utils.decode(jsonObject.getJSONObject("user").getString("nick"));

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("nick", nick);
                editor.commit();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (games != null) {

                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(MyProjectsRealmObject.class);
                    }
                });

                for (int i = 0; i < games.length(); i++) {
                    try {
                        JSONObject gameJsonObject = games.getJSONObject(i);

                        final String privateID       = gameJsonObject.getString("privateID");
                        final String publicID        = gameJsonObject.getString("publicID");

                        String title                 = Base64Utils.decode(gameJsonObject.getString("title"));
                        String updated                = gameJsonObject.getString("updated");
                        final String description     = Base64Utils.decode(gameJsonObject.getString("description"));
                        final boolean isPublic       = gameJsonObject.getString("public").equals("CHECKED");

                        final String likeCount       = gameJsonObject.getString("likes");
                        final String commentCount    = "0"; //jsonObject.getString("comment_count");
                        final String charCount       = gameJsonObject.getString("charcount");
                        final String code            = Base64Utils.decode(gameJsonObject.getString("code"));


                        if (title.equals("")) {
                            title = context.getString(R.string.unnamed);
                        }

                        updated = DateConversionUtils.convert(updated) + " | " + likeCount + " likes";



                        list.add(new MyProjectsObject(privateID, publicID, title, updated, description, isPublic, likeCount, commentCount, charCount, code));

                        final String finalTitle = title;
                        final String finalUpdated = updated;
                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                MyProjectsRealmObject object = realm.createObject(MyProjectsRealmObject.class);
                                object.setPrivateId(privateID);
                                object.setPublicId(publicID);
                                object.setTitle(finalTitle);
                                object.setUpdated(finalUpdated);
                                object.setDescription(description);
                                object.setIsPublic(isPublic);
                                object.setLikeCount(likeCount);
                                object.setCommentCount(commentCount);
                                object.setCharCount(charCount);
                                object.setCode(code);
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


            adapter.notifyDataSetChanged();

        } else {

            list.clear();

            RealmResults<MyProjectsRealmObject> realmObjects = Realm.getDefaultInstance().where(MyProjectsRealmObject.class).findAll();

            for (int i = 0; i < realmObjects.size(); i++) {

                String privateID    = realmObjects.get(i).getPrivateId();
                String publicID     = realmObjects.get(i).getPublicId();

                String title        = realmObjects.get(i).getTitle();
                String updated      = realmObjects.get(i).getUpdated();
                String description  = realmObjects.get(i).getDescription();
                boolean isPublic    = realmObjects.get(i).getIsPublic();

                String likeCount    = realmObjects.get(i).getLikeCount();
                String commentCount = realmObjects.get(i).getCommentCount();
                String charCount    = realmObjects.get(i).getCharCount();
                String code         = realmObjects.get(i).getCode();

                list.add(new MyProjectsObject(privateID, publicID, title, updated, description, isPublic, likeCount, commentCount, charCount, code));

            }

            adapter.notifyDataSetChanged();

        }
        
    }
}