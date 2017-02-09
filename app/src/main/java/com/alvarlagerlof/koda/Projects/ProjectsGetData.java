package com.alvarlagerlof.koda.Projects;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Utils.Base64Utils;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.alvarlagerlof.koda.Utils.DateConversionUtils;

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

class ProjectsGetData extends AsyncTask<Void, Void, String> {

    private Context context;
    private ArrayList<ProjectsObject> list;
    private RecyclerView.Adapter adapter;

    ProjectsGetData(Context context, ArrayList<ProjectsObject> list, RecyclerView.Adapter adapter) {
        this.context = context;
        this.list = list;
        this.adapter = adapter;
    }





    // Code here
    @Override
    final protected void onPreExecute() {

        list.clear();

        list.add(new ProjectsObject("", "", "",  "", "", false, "", "", ProjectsAdapter.TYPE_LOADING));
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

            JSONObject jsonObject = null;
            JSONArray games = null;
            String nick = null;



            if (games != null) {

                for (int i = 0; i < games.length(); i++) {
                    try {
                        JSONObject gameJsonObject = games.getJSONObject(i);

                        final String privateID       = gameJsonObject.getString("privateID");
                        final String publicID        = gameJsonObject.getString("publicID");

                        String title                 = Base64Utils.decode(gameJsonObject.getString("title"));
                        String updated               = DateConversionUtils.convert(gameJsonObject.getString("updated"));
                        final String description     = Base64Utils.decode(gameJsonObject.getString("description"));
                        final boolean isPublic       = gameJsonObject.getString("public").equals("CHECKED");

                        final String charCount       = gameJsonObject.getString("charcount");
                        final String code            = Base64Utils.decode(gameJsonObject.getString("code"));


                        if (title.equals("")) {
                            title = context.getString(R.string.unnamed);
                        }

                        list.add(new ProjectsObject(privateID, publicID, title, updated, description, isPublic, charCount, code, ProjectsAdapter.TYPE_ITEM));

                        final String finalTitle = title;
                        final String finalUpdated = updated;
                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                ProjectsRealmObject object = realm.createObject(ProjectsRealmObject.class);
                                object.setPrivateId(privateID);
                                object.setPublicId(publicID);
                                object.setTitle(finalTitle);
                                object.setUpdated(finalUpdated);
                                object.setDescription(description);
                                object.setIsPublic(isPublic);
                                object.setCharCount(charCount);
                                object.setCode(code);
                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (games.length() == 0) {
                    list.add(new ProjectsObject("", "", "", "", "", false, "", "", ProjectsAdapter.TYPE_NO_ITEMS));
                }
            }


            adapter.notifyDataSetChanged();

        } else {

            list.clear();

            RealmResults<ProjectsRealmObject> realmObjects = Realm.getDefaultInstance().where(ProjectsRealmObject.class).findAll();

            for (int i = 0; i < realmObjects.size(); i++) {

                String privateID    = realmObjects.get(i).getPrivateId();
                String publicID     = realmObjects.get(i).getPublicId();

                String title        = realmObjects.get(i).getTitle();
                String updated      = realmObjects.get(i).getUpdated();
                String description  = realmObjects.get(i).getDescription();
                boolean isPublic    = realmObjects.get(i).getIsPublic();

                String charCount    = realmObjects.get(i).getCharCount();
                String code         = realmObjects.get(i).getCode();

                list.add(new ProjectsObject(privateID, publicID, title, updated, description, isPublic, charCount, code, ProjectsAdapter.TYPE_ITEM));

            }

            if (realmObjects.size() == 0) {
                list.add(new ProjectsObject("", "", "", "", "", false, "", "", ProjectsAdapter.TYPE_NO_ITEMS));
            }

            adapter.notifyDataSetChanged();

        }
        
    }
}