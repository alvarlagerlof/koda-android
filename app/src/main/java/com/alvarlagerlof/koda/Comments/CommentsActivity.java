package com.alvarlagerlof.koda.Comments;

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

public class CommentsActivity extends AppCompatActivity {

    ArrayList<CommentsObject> list = new ArrayList<>();
    CommentsAdapter adapter = new CommentsAdapter(list);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activty);


        // Init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        getSupportActionBar().setTitle("Kommentarer på " + getIntent().getStringExtra("title"));


        // Init RecylerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setScrollContainer(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setAdapter(adapter);


        // Get data
        CommentsGetData dataTask = new CommentsGetData(this, getIntent().getStringExtra("publicID"), list, adapter);
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
