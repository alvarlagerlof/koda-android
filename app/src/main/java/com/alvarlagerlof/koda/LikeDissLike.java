package com.alvarlagerlof.koda;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

public class LikeDissLike {

    private static class MyTaskParams {
        Context context;
        String url;

        MyTaskParams(Context context, String url) {
            this.context = context;
            this.url = url;
        }
    }

    public static void like(Context context, String publicID) {
        MyTaskParams params = new MyTaskParams(context, "https://koda.nu/plus/" + publicID);

        runLikeDissLike runLikeDissLike = new runLikeDissLike();
        runLikeDissLike.execute(params);
    }

    public static void dislike(Context context, String publicID) {
        MyTaskParams params = new MyTaskParams(context, "https://koda.nu/minus/" + publicID);

        runLikeDissLike runLikeDissLike = new runLikeDissLike();
        runLikeDissLike.execute(params);
    }



    static class runLikeDissLike extends AsyncTask<MyTaskParams, Void, Void> {


        @Override
        protected Void doInBackground(MyTaskParams... params) {

            CookieHandler cookieHandler = new CookieManager(
                    new PersistentCookieStore(params[0].context), CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .build();

            Request request = new Request.Builder()
                    .url(params[0].url)
                    .build();


            try {
                Response response = client.newCall(request).execute();
                Log.d("response", response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

}
