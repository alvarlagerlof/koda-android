package com.alvarlagerlof.koda.MyProjects;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.Editor.EditorActivity;
import com.alvarlagerlof.koda.PrefValues;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 08/11/16.
 */

class MyProjectsAdd extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String title;
    private ArrayList<MyProjectsObject> list;
    private RecyclerView.Adapter adapter;

    MyProjectsAdd(Context context, String title, ArrayList<MyProjectsObject> list, RecyclerView.Adapter adapter) {
        this.context = context;
        this.title = title;
        this.list = list;
        this.adapter = adapter;
    }





    // Code here
    @Override
    final protected void onPreExecute() {

        String offlineId = "offline_" + String.valueOf(System.currentTimeMillis());
        final String finalOfflineId = offlineId;

        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                MyProjectsRealmObject object = realm.createObject(MyProjectsRealmObject.class);
                object.setPrivateId(finalOfflineId);
                object.setPublicId("");
                object.setTitle(title);
                object.setUpdated(String.valueOf(System.currentTimeMillis() / 1000L));
                object.setDescription("");
                object.setIsPublic(false);
                object.setLikeCount("0");
                object.setCommentCount("0");
                object.setCharCount("");
                object.setCode("<script src=\"http://koda.nu/simple.js\">\n" +
                        "\n" +
                        "  circle(100, 100, 20, \"red\");\n" +
                        "\n" +
                        "</script>");
            }
        });

        MyProjectsObject object = new MyProjectsObject(offlineId,
                "",
                title,
                String.valueOf(System.currentTimeMillis() / 1000L),
                "",
                false,
                "",
                "",
                "",
                "");

        list.add(0, object);

        for (int i = 0; i < list.size(); i++) {
            Log.d("title", list.get(i).title);
        }

        adapter.notifyDataSetChanged();

        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra("private_id", offlineId);
        intent.putExtra("public_id", "");
        intent.putExtra("code", "<script src=\"http://koda.nu/simple.js\">\n" +
                "\n" +
                "  circle(100, 100, 20, \"red\");\n" +
                "\n" +
                "</script>");
        context.startActivity(intent);

    }

    @Override
    final protected Void doInBackground(Void... progress) {
        if (ConnectionUtils.isConnected(context)) {
            try {

                CookieHandler cookieHandler = new CookieManager(
                        new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);

                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(new JavaNetCookieJar(cookieHandler))
                        .build();

                RequestBody formBody = new FormBody.Builder()
                        .add("title", title)
                        .build();

                Request request = new Request.Builder()
                        .url(PrefValues.URL_MY_PROJECTS_CREATE_NEW)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                response.body().close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    @Override
    final protected void onPostExecute(Void aVoid) {

    }
}