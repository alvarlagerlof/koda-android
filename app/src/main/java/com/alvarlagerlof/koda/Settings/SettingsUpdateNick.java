package com.alvarlagerlof.koda.Settings;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.PrefValues;

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
 * Created by alvar on 2016-11-09.
 */

class SettingsUpdateNick extends AsyncTask<Void, Void, Void> {

    Context context;
    String nick;

    SettingsUpdateNick(Context context, String nick) {
        this.context = context;
        this.nick = nick;
    }

    @Override
    protected Void doInBackground(Void... params) {

        // Save locally
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PrefValues.PREF_NICK, nick)
                .apply();


        // Save on the server
        if (ConnectionUtils.isConnected(context)) {
            CookieHandler cookieHandler = new CookieManager(
                    new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .build();

            FormBody data = new FormBody.Builder()
                    .add("nick", nick)
                    .build();

            Request request = new Request.Builder()
                    .url(PrefValues.URL_SETTINGS_SAVE_NICK)
                    .post(data)
                    .build();


            // TODO: DOES NOT SAVE THE NICKNAME ON THE SERVER

            try {
                Response response = client.newCall(request).execute();
                //Log.d("SettingsUpdateNick", response.body().string());
                response.body().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return null;
    }
}