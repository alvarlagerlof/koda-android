package com.alvarlagerlof.koda.Projects;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Utils.Base64Utils;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.arasthel.asyncjob.AsyncJob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-11-08.
 */

public class ProjectsSync extends AsyncTask<Object, Object, JSONArray> {

    private Realm realm = Realm.getDefaultInstance();
    private Context context;


    public ProjectsSync(Context context) {
        this.context = context;
    }


    @Override
    protected JSONArray doInBackground(Object... params) {

        JSONArray serverProjects;

        // Get all server projects
        if (ConnectionUtils.isConnected(context)) {
            try {

                CookieHandler cookieHandler = new CookieManager(
                        new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);

                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(new JavaNetCookieJar(cookieHandler))
                        .build();

                Request request = new Request.Builder()
                        .url(PrefValues.URL_MY_PROJECTS)
                        .build();

                Response response = client.newCall(request).execute();

                String json = response.body().string();
                response.body().close();


                // Create json objects and save nick
                if (!json.equals("")) {
                    JSONObject jsonObject = new JSONObject(json);

                    serverProjects = jsonObject.getJSONArray("games");

                    PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                            .putString("nick", Base64Utils.decode(jsonObject.getJSONObject("user").getString("nick")))
                            .apply();

                    return serverProjects;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        return null;
    }



    protected void onPostExecute(JSONArray serverProjects) {

        // Get all realm projects
        RealmResults<ProjectsRealmObject> realmProjects = Realm.getDefaultInstance().where(ProjectsRealmObject.class).findAll();


        // Loop over local projects
        for (int i = 0; i < realmProjects.size(); i++) {
            final ProjectsRealmObject realmProject = realmProjects.get(i);
            final JSONObject serverProject = getJsonObjectByPrivateId(serverProjects, realmProject.getPrivateId());


            if (serverProject != null) {

                // If it does exist on the server
                updateOldest(realmProject, serverProject);


            } else {

                // If it does NOT exist on the server

                new AsyncJob.AsyncJobBuilder<Boolean>()
                        .doInBackground(new AsyncJob.AsyncAction<Boolean>() {
                            @Override
                            public Boolean doAsync() {

                                try {

                                    ProjectsRealmObject realmProject = realm.where(ProjectsRealmObject.class)
                                            .equalTo("privateId", serverProject.getString("privateID"))
                                            .findFirst();


                                    Request request = new Request.Builder()
                                            .url(PrefValues.URL_MY_PROJECTS_CREATE_NEW)
                                            .post(new FormBody.Builder()
                                                    .add("title", realmProject.getTitle())
                                                    .add("description", realmProject.getDescription())
                                                    .add("author", "")
                                                    .add("public", String.valueOf(realmProject.getIsPublic()))
                                                    .add("code", realmProject.getCode())
                                                    .build())
                                            .build();


                                    Response response = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL))).build().newCall(request).execute();
                                    response.body().close();

                                } catch (java.io.IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                                return true;

                            }
                        }).create().start();
            }




        }


        // Loop over server projects if not empty
        if (serverProjects != null && serverProjects.length() > 0) {

            for (int i = 0; i < serverProjects.length(); i++) {

                try {

                    final JSONObject serverProject = serverProjects.getJSONObject(i);

                    ProjectsRealmObject realmProject = realm.where(ProjectsRealmObject.class)
                            .equalTo("privateId", serverProject.getString("privateID"))
                            .findFirst();


                    if (realmProject != null) {

                        // If it does exist on the server
                        updateOldest(realmProject, serverProject);


                    } else {

                        // If it does NOT exist on the realm

                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                ProjectsRealmObject object = realm.createObject(ProjectsRealmObject.class);

                                try {
                                    object.setPrivateId(serverProject.getString("privateID"));
                                    object.setPublicId(serverProject.getString("publicID"));
                                    object.setTitle(Base64Utils.decode(serverProject.getString("title").equals("") ? context.getString(R.string.unnamed) : serverProject.getString("title")));
                                    object.setUpdated(serverProject.getString("updated"));
                                    object.setDescription(Base64Utils.decode(serverProject.getString("description")));
                                    object.setIsPublic(serverProject.getString("public").equals("CHECKED"));
                                    object.setCode(Base64Utils.decode(serverProject.getString("code")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });                }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }

    }



























    private void updateOldest(final ProjectsRealmObject realmProject, final JSONObject serverProject) {

        try {
            if (Integer.parseInt(realmProject.getUpdated()) > Integer.parseInt(serverProject.getString("updated"))) {

                // If latest updated on realm


                new AsyncJob.AsyncJobBuilder<Boolean>()
                        .doInBackground(new AsyncJob.AsyncAction<Boolean>() {
                            @Override
                            public Boolean doAsync() {

                                Realm realm = Realm.getDefaultInstance();

                                try {


                                    // Code
                                    CookieHandler cookieHandler = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
                                    OkHttpClient client = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(cookieHandler)).build();

                                    RequestBody formBody = new FormBody.Builder()
                                            .add("code", realmProject.getCode())
                                            .build();

                                    Request request = new Request.Builder()
                                            .url("https://koda.nu/labbet/" + realmProject.getPrivateId())
                                            .post(formBody)
                                            .build();

                                    Response response = client.newCall(request).execute();
                                    response.body().close();



                                    // Meta data
                                    formBody = new FormBody.Builder()
                                            .add("title", realmProject.getTitle())
                                            .add("description", realmProject.getDescription())
                                            .add("author", "")
                                            .add("publicOrNot", realmProject.getIsPublic() ? "CHECKED" : "")
                                            .build();


                                    request = new Request.Builder()
                                            .url(PrefValues.URL_MY_PROJECTS_EDIT + realmProject.getPrivateId())
                                            .post(formBody)
                                            .build();


                                    response = client.newCall(request).execute();
                                    response.body().close();

                                } catch (java.io.IOException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                        }).create().start();
            }


            if (Integer.parseInt(serverProject.getString("updated")) > Integer.parseInt(realmProject.getUpdated())) {

                // If latest updated on server

                realm.beginTransaction();
                realmProject.setTitle(Base64Utils.decode(serverProject.getString("title")));
                realmProject.setUpdated(serverProject.getString("updated"));
                realmProject.setDescription(Base64Utils.decode(serverProject.getString("description")));
                realmProject.setIsPublic(serverProject.getString("public").equals("CHECKED"));
                realmProject.setCode(Base64Utils.decode(serverProject.getString("code")));
                realm.commitTransaction();

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




    private JSONObject getJsonObjectByPrivateId(JSONArray array, String privateId) {

        try {

            if (array != null && array.length() > 0 ) {
                for (int i = 0; i < array.length(); i++) {

                    if (array.getJSONObject(i)
                            .getString("privateID")
                            .equals(privateId)) {

                        return array.getJSONObject(i);
                    }


                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }



}


































/*// Check which has more projects, Server or Realm
        int serverProjectCount = 0;
        if (serverProjects != null) {
            serverProjectCount = serverProjects.length();
        }
        int realmProjectCount = realmProjects.size();

        int longestCount = serverProjectCount > realmProjectCount ? serverProjectCount : realmProjectCount;
        //int longestCountType = serverProjectCount > realmProjectCount ? TYPE_SERVER : TYPE_REALM;


        // Loop the longest
        for (int i = 0; i < longestCount; i++) {
            try {

                // If exists on both
                if (i < serverProjectCount && i < realmProjectCount) {

                    // Get JSONObject and Ream object
                    final JSONObject serverProject = (JSONObject) serverProjects.get(i);

                    Log.d("WHICH", (Integer.parseInt(serverProject.getString("updated")) > Integer.parseInt(realmProjects.get(i).getUpdated()) ? "server" : "realm") + " S: " + DateConversionUtils.convert(serverProject.getString("updated")) + " R: " +  DateConversionUtils.convert(realmProjects.get(i).getUpdated()));


                    if (Integer.parseInt(serverProject.getString("updated")) != Integer.parseInt(realmProjects.get(i).getUpdated())) {
                        if (Integer.parseInt(serverProject.getString("updated")) > Integer.parseInt(realmProjects.get(i).getUpdated())) {

                            // If most recently updated on Server
                            ProjectsRealmObject realmProjectSet = realm.where(ProjectsRealmObject.class)
                                    .equalTo("privateId", serverProject.getString("privateID"))
                                    .findFirst();

                            realm.beginTransaction();
                            realmProjectSet.setTitle(Base64Utils.decode(serverProject.getString("title")));
                            realmProjectSet.setUpdated(serverProject.getString("updated"));
                            realmProjectSet.setDescription(Base64Utils.decode(serverProject.getString("description")));
                            realmProjectSet.setIsPublic(serverProject.getString("public").equals("CHECKED"));
                            realmProjectSet.setCharCount(serverProject.getString("charcount"));
                            realmProjectSet.setCode(Base64Utils.decode(serverProject.getString("code")));
                            realm.commitTransaction();

                        } else if (Integer.parseInt(serverProject.getString("updated")) < Integer.parseInt(realmProjects.get(i).getUpdated())) {

                            Toast.makeText(context, "Is this running?", Toast.LENGTH_LONG).show();

                            // If most recently updated on Realm
                            new AsyncJob.AsyncJobBuilder<Boolean>()
                                    .doInBackground(new AsyncJob.AsyncAction<Boolean>() {
                                        @Override
                                        public Boolean doAsync() {

                                            Realm realm = Realm.getDefaultInstance();

                                            ProjectsRealmObject realmProject = null;
                                            try {
                                                realmProject = realm.where(ProjectsRealmObject.class)
                                                        .equalTo("privateId", serverProject.getString("privateID"))
                                                        .findFirst();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            // Code
                                            CookieHandler cookieHandler = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);
                                            OkHttpClient client = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(cookieHandler)).build();

                                            RequestBody formBody = new FormBody.Builder()
                                                    .add("code", realmProject.getCode())
                                                    .build();

                                            Request request = new Request.Builder()
                                                    .url("https://koda.nu/labbet/" + realmProject.getPrivateId())
                                                    .post(formBody)
                                                    .build();

                                            try {
                                                Response response = client.newCall(request).execute();
                                                response.body().close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }




                                            // Meta data
                                            formBody = new FormBody.Builder()
                                                    .add("title", realmProject.getTitle())
                                                    .add("description", realmProject.getDescription())
                                                    .add("author", "")
                                                    .add("publicOrNot", realmProject.getIsPublic() ? "CHECKED" : "")
                                                    .build();


                                            request = new Request.Builder()
                                                    .url(PrefValues.URL_MY_PROJECTS_EDIT + realmProject.getPrivateId())
                                                    .post(formBody)
                                                    .build();

                                            try {
                                                Response response = client.newCall(request).execute();
                                                response.body().close();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            return true;
                                        }
                                    }).create().start();




                        }


                    }
                    }





                // Only on Server
                if (i >= realmProjectCount) {

                    final JSONObject serverProject = (JSONObject) serverProjects.get(i);

                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            ProjectsRealmObject object = realm.createObject(ProjectsRealmObject.class);

                            try {
                                object.setPrivateId(serverProject.getString("privateID"));
                                object.setPublicId(serverProject.getString("publicID"));
                                object.setTitle(Base64Utils.decode(serverProject.getString("title").equals("") ? context.getString(R.string.unnamed) : serverProject.getString("title")));
                                object.setUpdated(serverProject.getString("updated"));
                                object.setDescription(Base64Utils.decode(serverProject.getString("description")));
                                object.setIsPublic(serverProject.getString("public").equals("CHECKED"));
                                object.setCharCount(serverProject.getString("charcount"));
                                object.setCode(Base64Utils.decode(serverProject.getString("code")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }


                // Only on Realm
                if (i >= serverProjectCount) {
                    // TODO: ADD TO SERVER
                    // TODO: GET MICKE TO ADD IT TO THE API
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/