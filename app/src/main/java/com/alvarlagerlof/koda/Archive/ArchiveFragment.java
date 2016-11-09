package com.alvarlagerlof.koda.Archive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alvarlagerlof.koda.DividerItemDecoration;
import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created author alvar on 2016-07-02.
 */
public class ArchiveFragment extends Fragment {

    ArchiveAdapter adapter;
    ArrayList<ArchiveObject> list = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.archive_fragment, container, false);

        adapter = new ArchiveAdapter(list, getActivity().getSupportFragmentManager(), getContext());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setAdapter(adapter);


        // Get data
        ArchiveGetData dataTask = new ArchiveGetData(getContext(), list, adapter);
        dataTask.execute();

        return view;
    }


}
