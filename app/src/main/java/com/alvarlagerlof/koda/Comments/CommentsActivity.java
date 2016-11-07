package com.alvarlagerlof.koda.Comments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alvarlagerlof.koda.DividerItemDecoration;
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

public class CommentsActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    CommentsAdapter commentsAdapter;
    ArrayList<CommentsObject> list = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);

        getSupportActionBar().setTitle("Kommentarer på " + getIntent().getStringExtra("title"));

        list.add(new CommentsObject("", "", ""));

        commentsAdapter = new CommentsAdapter(list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setScrollContainer(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setAdapter(commentsAdapter);

        getData getData = new getData();
        getData.execute();

    }


    class getData extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list.add(new CommentsObject("Loading", "", ""));
            commentsAdapter.notifyDataSetChanged();
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            JSONArray json = null;
            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://ravla.org/comments.php")
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();


                json = new JSONArray(result);

            } catch (Exception e) {}

            return json;
        }


        protected void onPostExecute(JSONArray jsonArray) {

            if (jsonArray != null) {

                list.remove(list.size() - 1);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String by      = jsonObject.getString("author");
                        String date    = jsonObject.getString("date");
                        String comment = jsonObject.getString("comment");


                        if (jsonObject.isNull("author")) {
                            by = getString(R.string.anonymous);
                        } else if (by.equals("")) {
                            by = getString(R.string.anonymous);
                        }

                        list.add(new CommentsObject(by, date, comment));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                commentsAdapter.notifyDataSetChanged();

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
