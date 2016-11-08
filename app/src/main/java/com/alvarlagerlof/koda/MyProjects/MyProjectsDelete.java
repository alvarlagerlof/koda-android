package com.alvarlagerlof.koda.MyProjects;

import android.content.Context;
import android.os.AsyncTask;

import com.alvarlagerlof.koda.ConnectionUtils;
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.MainAcitivty;
import com.alvarlagerlof.koda.PrefValues;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 08/11/16.
 */

class MyProjectsDelete extends AsyncTask<Void, Void, Void> {
    interface MyProjectsDeleteListener {
        void onPreExecuteConcluded();
        void onPostExecuteConcluded();
    }

    private Context context;
    private String privateID;
    private int position;
    private MyProjectsDeleteListener listener;

    final void setListener(Context context, String privateID, int position, MyProjectsDeleteListener listener) {
        this.context = context;
        this.privateID = privateID;
        this.position = position;
        this.listener = listener;
    }





    // Code here
    @Override
    final protected void onPreExecute() {
        MainAcitivty.fragmentMyProjects.removeItemAt(position);

        if (listener != null) listener.onPreExecuteConcluded();
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

                Request request = new Request.Builder()
                        .url(PrefValues.URL_MY_PROJECTS_DELETE + privateID)
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
        if (listener != null) listener.onPostExecuteConcluded();
    }
}