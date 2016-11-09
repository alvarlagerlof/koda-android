package com.alvarlagerlof.koda;

import android.content.Context;
import android.os.AsyncTask;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-10-15.
 */

public class LikeDissLike extends AsyncTask<Void, Void, String> {

    private Context context;
    private String url;

    public LikeDissLike(Context context, String url, String publicID) {
        this.context = context;
        this.url = url + publicID;
    }


    @Override
    protected String doInBackground(Void... params) {
        CookieHandler cookieHandler = new CookieManager(
                new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieHandler))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            response.body().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

