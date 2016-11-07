package com.alvarlagerlof.koda.MyProjects;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.DividerItemDecoration;
import com.alvarlagerlof.koda.EditorActivity;
import com.alvarlagerlof.koda.FastBase64;
import com.alvarlagerlof.koda.NiceDate;
import com.alvarlagerlof.koda.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-07-02.
 */
public class FragmentMyProjects extends Fragment {

    RecyclerView recyclerView;
    MyProjectsAdapter myProjectsAdapter;
    FloatingActionButton floatingActionButton;

    ArrayList<MyProjectsObject> projectsList = new ArrayList<>();

    Realm realm;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_projects, container, false);

        realm = Realm.getDefaultInstance();

        projectsList.add(new MyProjectsObject("", "", "", "", "", false, "", "", "", ""));

        myProjectsAdapter = new MyProjectsAdapter(projectsList, getActivity().getSupportFragmentManager());

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setScrollContainer(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setAdapter(myProjectsAdapter);

        final getData getData = new getData();
        getData.execute("");


        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new createNew().execute("");
            }
        });


        return view;
    }



    public class getData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            projectsList.add(new MyProjectsObject("Loading", "", "",  "", "", false, "", "", "", ""));
            myProjectsAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... strings) {
            String json = null;
            try {

                CookieHandler cookieHandler = new CookieManager(
                        new PersistentCookieStore(getContext()), CookiePolicy.ACCEPT_ALL);

                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(new JavaNetCookieJar(cookieHandler))
                        .build();


                Request request = new Request.Builder()
                        .url("https://koda.nu/labbet_json")
                        .build();
                Response response = client.newCall(request).execute();

                json = response.body().string();

            } catch (Exception e) {}

            return json;
        }


        protected void onPostExecute(String json) {

            if (json != null) {

                projectsList.remove(projectsList.size() - 1);

                JSONObject jsonObject = null;
                JSONArray games = null;
                String nick = null;

                try {
                    jsonObject = new JSONObject(json);

                    games = jsonObject.getJSONArray("games");
                    nick = FastBase64.decode(jsonObject.getJSONObject("user").getString("nick"));

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("nick", nick);
                    editor.commit();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (games != null) {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(MyProjectsRealmObject.class);
                        }
                    });

                    for (int i = 0; i < games.length(); i++) {
                        try {
                            JSONObject gameJsonObject = games.getJSONObject(i);

                            final String privateID       = gameJsonObject.getString("privateID");
                            final String publicID        = gameJsonObject.getString("publicID");

                            String title                 = FastBase64.decode(gameJsonObject.getString("title"));
                            String updated                = gameJsonObject.getString("updated");
                            final String description     = FastBase64.decode(gameJsonObject.getString("description"));
                            final boolean isPublic       = false; //gameJsonObject.getBoolean("is_public");

                            final String likeCount       = gameJsonObject.getString("likes");
                            final String commentCount    = "0"; //jsonObject.getString("comment_count");
                            final String charCount       = gameJsonObject.getString("charcount");
                            final String code            = FastBase64.decode(gameJsonObject.getString("code"));


                            if (title.equals("")) {
                                title = getContext().getString(R.string.unnamed);
                            }

                            updated = NiceDate.convert(updated) + " | " + likeCount + " likes";



                            projectsList.add(new MyProjectsObject(privateID, publicID, title, updated, description, isPublic, likeCount, commentCount, charCount, code));

                            final String finalTitle = title;
                            final String finalUpdated = updated;
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    MyProjectsRealmObject object = realm.createObject(MyProjectsRealmObject.class);
                                    object.setPrivateId(privateID);
                                    object.setPublicId(publicID);
                                    object.setTitle(finalTitle);
                                    object.setUpdated(finalUpdated);
                                    object.setDescription(description);
                                    object.setIsPublic(isPublic);
                                    object.setLikeCount(likeCount);
                                    object.setCommentCount(commentCount);
                                    object.setCharCount(charCount);
                                    object.setCode(code);
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


                myProjectsAdapter.notifyDataSetChanged();

            } else {

                projectsList.remove(projectsList.size() - 1);

                RealmResults<MyProjectsRealmObject> realmObjects = realm.where(MyProjectsRealmObject.class).findAll();

                for (int i = 0; i < realmObjects.size(); i++) {

                    String privateID    = realmObjects.get(i).getPrivateId();
                    String publicID     = realmObjects.get(i).getPublicId();

                    String title        = realmObjects.get(i).getTitle();
                    String updated      = realmObjects.get(i).getUpdated();
                    String description  = realmObjects.get(i).getDescription();
                    boolean isPublic    = realmObjects.get(i).getIsPublic();

                    String likeCount    = realmObjects.get(i).getLikeCount();
                    String commentCount = realmObjects.get(i).getCommentCount();
                    String charCount    = realmObjects.get(i).getCharCount();
                    String code         = realmObjects.get(i).getCode();

                    projectsList.add(new MyProjectsObject(privateID, publicID, title, updated, description, isPublic, likeCount, commentCount, charCount, code));

                }

                myProjectsAdapter.notifyDataSetChanged();

            }

        }

    }

    class createNew extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            String json = null;
            try {

                CookieHandler cookieHandler = new CookieManager(
                        new PersistentCookieStore(getContext()), CookiePolicy.ACCEPT_ALL);

                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(new JavaNetCookieJar(cookieHandler))
                        .build();


                Request request = new Request.Builder()
                        .url("https://koda.nu/skapajson")
                        .build();
                Response response = client.newCall(request).execute();
                json = response.body().string();;

            } catch (Exception e) {

            }

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonObject;
        }


        protected void onPostExecute(JSONObject jsonObject) {

            if (jsonObject != null) {

                try {

                    Intent intent = new Intent(getActivity(), EditorActivity.class);
                    intent.putExtra("private_id", jsonObject.getString("private_id"));
                    intent.putExtra("public_id", jsonObject.getString("public_id"));
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}
