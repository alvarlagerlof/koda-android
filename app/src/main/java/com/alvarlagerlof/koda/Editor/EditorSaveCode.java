package com.alvarlagerlof.koda.Editor;

import android.os.AsyncTask;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.MyProjects.MyProjectsRealmObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.internal.Context;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 08/11/16.
 */

public class EditorSaveCode {

    class Async extends AsyncTask<Void, Void, Void> {

        public ArrayList<Object> paramsList;

        @Override
        protected Void doInBackground(Void[] voids) {

            // Send to server
            CookieHandler cookieHandler = new CookieManager(new PersistentCookieStore((android.content.Context) paramsList.get(0)), CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("code", String.valueOf(paramsList.get(1)))
                    .build();


            Request request = new Request.Builder()
                    .url("https://koda.nu/labbet/" + String.valueOf(paramsList.get(2)))
                    .post(formBody)
                    .build();


            try {
                Response response = client.newCall(request).execute();
                response.body().close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Save locally
            final Realm realm = Realm.getDefaultInstance();

            MyProjectsRealmObject object = realm.where(MyProjectsRealmObject.class)
                    .equalTo("privateId", String.valueOf(paramsList.get(2)))
                    .findFirst();

            realm.beginTransaction();
            object.setCode(String.valueOf(paramsList.get(1)));
            realm.commitTransaction();
        }
    }
}


