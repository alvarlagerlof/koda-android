package com.alvarlagerlof.koda.Editor;

import android.content.Context;
import android.os.AsyncTask;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.Projects.ProjectsRealmObject;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

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

class EditorSave extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String privateID;
    private String code;

    EditorSave(Context context, String privateID, String code) {
        this.context = context;
        this.privateID = privateID;
        this.code = code;
    }





    // Code here
    @Override
    final protected void onPreExecute() {

    }

    @Override
    final protected Void doInBackground(Void... progress) {
        if (ConnectionUtils.isConnected(context)) {

            // Send to server
            CookieHandler cookieHandler = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
            OkHttpClient client = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(cookieHandler)).build();

            RequestBody formBody = new FormBody.Builder()
                    .add("code", code)
                    .build();

            Request request = new Request.Builder()
                    .url("https://koda.nu/labbet/" + privateID)
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                response.body().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return null;
    }


    @Override
    final protected void onPostExecute(Void aVoid) {

        // Save locally
        Realm realm = Realm.getDefaultInstance();
        ProjectsRealmObject object = realm.where(ProjectsRealmObject.class)
                .equalTo("privateId", privateID)
                .findFirst();

        realm.beginTransaction();
        object.setCode(code);
        realm.commitTransaction();

    }
}