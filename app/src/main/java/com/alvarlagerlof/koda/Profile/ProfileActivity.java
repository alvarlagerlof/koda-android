package com.alvarlagerlof.koda.Profile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.alvarlagerlof.koda.DividerItemDecoration;
import com.alvarlagerlof.koda.R;

import java.util.ArrayList;

/**
 * Created by alvar on 2016-08-15.
 */
public class ProfileActivity extends AppCompatActivity {

    Toolbar toolbar;

    ArrayList<ProfileObject> list = new ArrayList<>();
    ProfileAdapter profileAdapter = new ProfileAdapter(list, getSupportFragmentManager(), ProfileActivity.this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        // Init toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);


        // Init RecylerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setScrollContainer(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setAdapter(profileAdapter);


        // Get data
        ProfileGetData dataTask = new ProfileGetData();
        dataTask.setParams(this, list, profileAdapter);
        dataTask.execute();

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
