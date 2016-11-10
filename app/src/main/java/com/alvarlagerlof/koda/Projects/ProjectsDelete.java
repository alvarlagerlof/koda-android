package com.alvarlagerlof.koda.Projects;

import android.content.Context;
import android.os.AsyncTask;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.MainAcitivty;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 08/11/16.
 */

class ProjectsDelete extends AsyncTask<Void, Void, Void> {

    private Context context;
    private String privateID;
    private int position;

    ProjectsDelete(Context context, String privateID, int position) {
        this.context = context;
        this.privateID = privateID;
        this.position = position;
    }





    // Code here
    @Override
    final protected void onPreExecute() {
        MainAcitivty.fragmentMyProjects.removeItemAt(position);

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ProjectsRealmObject> result = realm.where(ProjectsRealmObject.class).equalTo("privateId", privateID).findAll();
                result.deleteAllFromRealm();
            }
        });

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
    }
}