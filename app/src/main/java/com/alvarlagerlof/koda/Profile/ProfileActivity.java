package com.alvarlagerlof.koda.Profile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alvarlagerlof.koda.DividerItemDecoration;
import com.alvarlagerlof.koda.NiceDate;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alvar on 2016-08-15.
 */
public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    ProfileAdapter profileAdapter;
    ArrayList<ProfileObject> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);



        list.add(new ProfileObject("", "", "", "Loading", "", "", "", "", false, ""));

        profileAdapter = new ProfileAdapter(list, getSupportFragmentManager(), ProfileActivity.this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setScrollContainer(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setAdapter(profileAdapter);

        getData getData = new getData();
        getData.execute();

    }


    class getData extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            profileAdapter.notifyDataSetChanged();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject json = null;
            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(PrefValues.URL_PROFILE)
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();


                json = new JSONObject(result);

            } catch (Exception e) {}

            return json;
        }


        protected void onPostExecute(JSONObject jsonObject) {

            if (jsonObject != null) {

                list.remove(list.size() - 1);



                try {

                    String authorTitle      = jsonObject.getJSONObject("profile").getString("author");
                    String numberOfProjects = jsonObject.getJSONObject("profile").getString("numberOfProjects");

                    list.add(new ProfileObject("", "", authorTitle, "", "", "", "", "", false, numberOfProjects));


                    JSONArray projectsJsonArray = jsonObject.getJSONArray("projects");

                        for (int i = 0; i < projectsJsonArray.length(); i++) {

                            JSONObject projectsJsonObject = projectsJsonArray.getJSONObject(i);

                            String title          = projectsJsonObject.getString("title");
                            String author         = projectsJsonObject.getString("author");
                            String description    = projectsJsonObject.getString("description");
                            String updated        = projectsJsonObject.getString("updated");
                            String publicID       = projectsJsonObject.getString("publicID");
                            String like_count     = projectsJsonObject.getString("like_count");
                            String comment_count  = projectsJsonObject.getString("comment_count");
                            String char_count     = projectsJsonObject.getString("char_count");

                            if (projectsJsonObject.isNull("author")) {
                                author = getString(R.string.anonymous);
                            } else if (author.equals("")) {
                                author = getString(R.string.anonymous);
                            }

                            updated = "Uppdaterad " + NiceDate.convert(updated);

                            list.add(new ProfileObject(publicID, title, updated, description, updated, like_count, comment_count, char_count, true, ""));

                        }

                    profileAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }





            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
