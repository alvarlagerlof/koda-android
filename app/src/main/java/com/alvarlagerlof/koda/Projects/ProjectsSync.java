package com.alvarlagerlof.koda.Projects;

import android.content.Context;
import android.content.Intent;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.Editor.EditorActivity;
import com.alvarlagerlof.koda.RemoteConfigValues;
import com.alvarlagerlof.koda.Utils.Base64Utils;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.arasthel.asyncjob.AsyncJob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-11-08.
 */

public class ProjectsSync {

    private Context context;
    private String openPrivateID = null;


    public ProjectsSync(Context context) {
        this.context = context;

        if (ConnectionUtils.isConnected(context)) {
            sendToServer();
        }

    }


    public ProjectsSync(Context context, String openPrivateID) {
        this.context = context;
        this.openPrivateID = openPrivateID;

        if (ConnectionUtils.isConnected(context)) {
            sendToServer();
        } else {

            Intent intent = new Intent(context, EditorActivity.class);
            intent.putExtra("privateID", openPrivateID);
            intent.putExtra("publicID", openPrivateID);
            context.startActivity(intent);
        }



    }


    private void sendToServer() {
        new AsyncJob.AsyncJobBuilder<JSONObject>()
                .doInBackground(new AsyncJob.AsyncAction<JSONObject>() {
                    @Override
                    public JSONObject doAsync() {

                            try {

                                // Get all Realm projects
                                RealmResults<ProjectsRealmObject> realmProjects = Realm.getDefaultInstance().where(ProjectsRealmObject.class).findAll();


                                // Create a json array
                                final JSONArray projects = new JSONArray();


                                // Loop over Realm projects
                                for (int i = 0; i < realmProjects.size(); i++) {

                                    ProjectsRealmObject realmObject = realmProjects.get(i);
                                    JSONObject jsonObject = new JSONObject();


                                    jsonObject.put("privateID", realmObject.getprivateID());
                                    jsonObject.put("updated", realmObject.getUpdatedServer());

                                    if (!realmObject.getSynced()) {
                                        jsonObject.put("title",       Base64Utils.encode(realmObject.getTitle()));
                                        jsonObject.put("description", Base64Utils.encode(realmObject.getDescription()));
                                        jsonObject.put("public",      realmObject.getIsPublic());
                                        jsonObject.put("code",        Base64Utils.encode(realmObject.getCode()));
                                    }

                                    projects.put(jsonObject);

                                }




                                // Send it
                                Request request = new Request.Builder()
<<<<<<< Updated upstream
                                        .url(RemoteConfigValues.URL_SYNC)
=======
                                        .url(PrefValues.URL_PROJECTS_SYNC)
>>>>>>> Stashed changes
                                        .post(new FormBody.Builder()
                                                .add("projects", projects.toString())
                                                .build())
                                        .build();

                                Response response = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL))).build().newCall(request).execute();


                                // Response to string
                                String stringResponse = response.body().string();

                                Log.d("response", RemoteConfigValues.URL_SYNC);


                                Log.d("response", stringResponse);

                                // Close response
                                response.body().close();

                                return new JSONObject(stringResponse);


                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }


                            return null;
                    }
                })
                .doWhenFinished(new AsyncJob.AsyncResultAction<JSONObject>() {
                    @Override
                    public void onResult(JSONObject response) {
                        dealWithRespose(response);
                    }
                }).create().start();
    }





    private void dealWithRespose(final JSONObject response) {

        new AsyncJob.AsyncJobBuilder<JSONObject>()
                .doInBackground(new AsyncJob.AsyncAction<JSONObject>() {
                    @Override
                    public JSONObject doAsync() {

                        try {

                            Realm realm = Realm.getDefaultInstance();


                            // If response not empty
                            if (response != null) {

                                // Loop over response
                                for (int i = 0; i < response.getJSONArray("projects").length(); i++) {

                                    // Get project
                                    JSONObject responseItem = response.getJSONArray("projects").getJSONObject(i);


                                    // Get Realm project based on privateID
                                    ProjectsRealmObject realmProject = Realm.getDefaultInstance().where(ProjectsRealmObject.class).equalTo("privateID", responseItem.getString("old_privateID")).findFirst();


                                    if (realmProject != null) {

                                        // Edit the existing one
                                        realm.beginTransaction();
                                        realmProject.setprivateID(responseItem.getString("privateID"));
                                        realmProject.setpublicID(responseItem.getString("publicID"));
                                        realmProject.setTitle(Base64Utils.decode(responseItem.getString("title")));
                                        realmProject.setDescription(Base64Utils.decode(responseItem.getString("description")));
                                        realmProject.setCode(Base64Utils.decode(responseItem.getString("code")));
                                        realmProject.setSynced(true);
                                        realmProject.setIsPublic(responseItem.getBoolean("public"));
                                        realmProject.setUpdatedServer(responseItem.getString("updated"));
                                        realmProject.setUpdatedRealm(responseItem.getString("updated"));
                                        realm.commitTransaction();

                                    } else {

                                        // Create a new one
                                        realm.beginTransaction();
                                        ProjectsRealmObject object = realm.createObject(ProjectsRealmObject.class); // Create a new object
                                        object.setprivateID(responseItem.getString("privateID"));
                                        object.setpublicID(responseItem.getString("publicID"));
                                        object.setTitle(Base64Utils.decode(responseItem.getString("title")));
                                        object.setDescription(Base64Utils.decode(responseItem.getString("description")));
                                        object.setCode(Base64Utils.decode(responseItem.getString("code")));
                                        object.setSynced(true);
                                        object.setIsPublic(responseItem.getBoolean("public"));
                                        object.setUpdatedServer(responseItem.getString("updated"));
                                        object.setUpdatedRealm(responseItem.getString("updated"));
                                        realm.commitTransaction();


                                    }


                                    // Open new project
                                    if (openPrivateID != null && openPrivateID.equals(responseItem.getString("old_privateID"))) {
                                        Intent intent = new Intent(context, EditorActivity.class);
                                        intent.putExtra("privateID", responseItem.getString("privateID"));
                                        intent.putExtra("publicID", responseItem.getString("publicID"));
                                        context.startActivity(intent);
                                    }



                                }
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        return null;
                    }
                }).create().start();


    }


}