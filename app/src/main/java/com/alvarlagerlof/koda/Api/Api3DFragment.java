package com.alvarlagerlof.koda.Api;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvarlagerlof.koda.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by alvar on 2016-07-02.
 */
public class Api3DFragment extends Fragment {

    RecyclerView recyclerView;
    Api3DAdapter api3dAdapter;
    ArrayList<ApiObject> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.api_tab, container, false);

        list = new ArrayList<>();
        list.add(new ApiObject("Loading", ""));

        api3dAdapter = new Api3DAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(api3dAdapter);

        getData getData = new getData();
        getData.execute("3D");

        return view;
    }


    class getData extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... strings) {
            JSONArray json = null;
            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://ravla.org/api.php?dimension=" + strings[0])
                        .build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();


                json = new JSONArray(result);

            } catch (Exception e) {
            }

            return json;
        }


        protected void onPostExecute(JSONArray jsonArray) {

            if (jsonArray != null) {

                list.remove(0);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        String command = jsonobject.getString("command");
                        String description = jsonobject.getString("description");
                        list.add(new ApiObject(command, description));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                api3dAdapter.notifyDataSetChanged();


            }
        }

    }


}
