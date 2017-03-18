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
import com.alvarlagerlof.koda.Utils.DateConversionUtils;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by alvar on 2016-07-02.
 */

public class ProjectsFragment extends Fragment {

    ProjectsAdapter adapter;
    ArrayList<Object> projectsList = new ArrayList<>();


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Init
        View view = inflater.inflate(R.layout.projects_fragment, container, false);


        // Recyclerview
        adapter = new ProjectsAdapter(projectsList, getActivity().getSupportFragmentManager());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
        recyclerView.setAdapter(adapter);


        // On FAB click
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ProjectsAddDialog(getContext());
            }
        });


        // Get data once
        getData();

        // On database change
        Realm.getDefaultInstance().addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                getData();
            }
        });





        return view;
    }




    public void getData() {

        // Clear adapter list
        projectsList.clear();


        // Get all objects and add to list
        RealmResults<ProjectsRealmObject> realmObjects = Realm.getDefaultInstance().where(ProjectsRealmObject.class).findAll()
                .sort("updatedRealm", Sort.DESCENDING);
        for (int i = 0; i < realmObjects.size(); i++) {

            String privateID    = realmObjects.get(i).getprivateID();
            String publicID     = realmObjects.get(i).getpublicID();
            String title        = realmObjects.get(i).getTitle();
            String updated      = DateConversionUtils.convert(realmObjects.get(i).getUpdatedRealm());
            String description  = realmObjects.get(i).getDescription();
            boolean isPublic    = realmObjects.get(i).getIsPublic();
            String code         = realmObjects.get(i).getCode();
            projectsList.add(new ProjectsObjectData(privateID, publicID, title, updated, description, isPublic, code));
        }



        // If no objects
        if (realmObjects.size() == 0) {
            projectsList.add(new ProjectsObjectNoItems());
        }


        // Notify adapter
        adapter.notifyDataSetChanged();
    }


}
