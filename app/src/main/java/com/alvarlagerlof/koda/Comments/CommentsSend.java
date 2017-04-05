package com.alvarlagerlof.koda.Comments;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.RemoteConfigValues;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2017-01-28.
 */

class CommentsSend extends AsyncTask<Void, Void, String> {

    private Context context;
    private String url;
    private String comment;

    CommentsSend(Context context, String publicID, String comment) {
        this.context = context;
        this.url = RemoteConfigValues.URL_COMMENTS_SEND + publicID;
        this.comment = comment;
    }


    @Override
    protected String doInBackground(Void... params) {

        Log.d("URL", url);


        CookieHandler cookieHandler = new CookieManager(
                new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieHandler))
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("comment", comment)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        try {
            Response response = client.newCall(request).execute();
            Log.d("RESPONSE", response.body().string());
            Toast.makeText(context, response.body().string(), Toast.LENGTH_LONG).show();


            response.body().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

