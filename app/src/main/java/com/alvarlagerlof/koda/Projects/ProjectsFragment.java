package com.alvarlagerlof.koda.Projects;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
 * Created by alvar on 2016-07-02.
 */
public class ProjectsFragment extends Fragment {

    ProjectsAdapter adapter;
    ArrayList<ProjectsObject> projectsList = new ArrayList<>();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        adapter = new ProjectsAdapter(projectsList, getActivity().getSupportFragmentManager());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setAdapter(adapter);


        // Get data
        ProjectsGetData dataTask = new ProjectsGetData(getContext(), projectsList, adapter);
        dataTask.execute();


        // Add button
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ProjectsAddDialog(getContext(), projectsList, adapter);
            }
        });

        return view;
    }


    public void removeItemAt(int pos) {
        projectsList.remove(pos);
        adapter.notifyDataSetChanged();
    }


}
