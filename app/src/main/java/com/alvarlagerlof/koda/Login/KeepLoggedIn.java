package com.alvarlagerlof.koda.Login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.Projects.ProjectsSync;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-10-15.
 */

public class KeepLoggedIn extends AsyncTask<Void, Void, String> {

    private ProgressDialog progressDialog;
    private Context context;

    public KeepLoggedIn(Context context) {
        this.context = context;
    }




    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loggar in...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        if (new PersistentCookieStore(context).getCookies().size() < 1 || PreferenceManager.getDefaultSharedPreferences(context).getString(PrefValues.PREF_EMAIL, null) == null) {
            context.startActivity(new Intent(context, LoginActivity.class));
        }

        if (ConnectionUtils.isConnected(context)) {
            CookieHandler cookieHandler = new CookieManager(
                    new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", PreferenceManager.getDefaultSharedPreferences(context).getString(PrefValues.PREF_EMAIL, ""))
                    .add("password", PreferenceManager.getDefaultSharedPreferences(context).getString(PrefValues.PREF_EMAIL, ""))
                    .add("headless", "thisIsHeadLess")
                    .build();

            Request request = new Request.Builder()
                    .url(PrefValues.URL_LOGIN)
                    .post(formBody)
                    .build();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (PreferenceManager.getDefaultSharedPreferences(context).getString(PrefValues.PREF_EMAIL, null) != null) {
            new ProjectsSync(context).execute();
        }
    }
}

