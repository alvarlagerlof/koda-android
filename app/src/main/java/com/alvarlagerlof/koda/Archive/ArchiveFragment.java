package com.alvarlagerlof.koda.Archive;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.DividerItemDecoration;
import com.alvarlagerlof.koda.FastBase64;
import com.alvarlagerlof.koda.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-07-02.
 */
public class ArchiveFragment extends Fragment {

    RecyclerView recyclerView;
    ArchiveAdapter archiveAdapter;
    ArrayList<ArchiveObject> list = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archive, container, false);

        list.add(new ArchiveObject("", "", "", "", "", "", "", "", false));

        archiveAdapter = new ArchiveAdapter(list, getActivity().getSupportFragmentManager(), getContext());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setScrollContainer(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setAdapter(archiveAdapter);

        getData getData = new getData();
        getData.execute("0");


        return view;
    }

    class getData extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            list.add(new ArchiveObject("Loading", "", "", "", "", "", "", "", false));
            archiveAdapter.notifyDataSetChanged();
        }

        @Override
        protected JSONArray doInBackground(String... strings) {
            JSONArray json = null;
            try {

                CookieHandler cookieHandler = new CookieManager(
                        new PersistentCookieStore(getContext()), CookiePolicy.ACCEPT_ALL);

                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(new JavaNetCookieJar(cookieHandler))
                        .build();


                Request request = new Request.Builder()
                        .url("https://koda.nu/arkivet_json/" + strings[0])
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

                        String title        = FastBase64.decode(jsonObject.getString("title"));
                        String author       = FastBase64.decode(jsonObject.getString("author"));
                        String description  = FastBase64.decode(jsonObject.getString("description"));
                        String updated      = jsonObject.getString("updated");
                        String publicID     = jsonObject.getString("publicID");
                        String likesCount   = jsonObject.getString("likes");
                        String commentCount = String.valueOf(new Random().nextInt(100) + 1);
                        String charCount    = String.valueOf(new Random().nextInt(400) + 1);
                        Boolean liked       = jsonObject.getBoolean("liked");

                        if (title.equals("")) title = getContext().getString(R.string.unnamed);
                        if (author.equals("")) author = getContext().getString(R.string.anonymous);
                        if (description.equals("")) description = getContext().getString(R.string.no_description);


                        list.add(new ArchiveObject(publicID, title, author, description, updated, likesCount, commentCount, charCount, liked));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                archiveAdapter.notifyDataSetChanged();

            }
        }
    }

}
