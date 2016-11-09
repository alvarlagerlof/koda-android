package com.alvarlagerlof.koda.Api;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created by alvar on 2016-07-02.
 */
public class ApiTabFragment extends Fragment {

    public String url;

    ApiAdapter adapter;
    ArrayList<ApiObject> list = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.api_tab, container, false);

        adapter = new ApiAdapter(list);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Get data
        ApiGetData dataTask = new ApiGetData(getContext(), url, list, adapter);
        dataTask.execute();

        return view;
    }

}
