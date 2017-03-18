package com.alvarlagerlof.koda.Comments;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alvarlagerlof.koda.DividerItemDecoration;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alvar on 2016-08-15.
 */

public class CommentsActivity extends AppCompatActivity {

    ArrayList<CommentsObject> list = new ArrayList<>();
    CommentsAdapter adapter = new CommentsAdapter(list);

    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText comment_edittext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_activty);

        comment_edittext = (EditText) findViewById(R.id.comment_edittext);


        // Init toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        getSupportActionBar().setTitle("Kommentarer på " + getIntent().getStringExtra("title"));


        // Init RecylerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setAdapter(adapter);


        // Get data
        CommentsGetData dataTask = new CommentsGetData(this, getIntent().getStringExtra("publicID"), list, adapter);
        dataTask.execute();

    }


    public void addComment(View view) {

        if (ConnectionUtils.isConnected(this)) {

            // Send to server
            new CommentsSend(this,
                             getIntent().getStringExtra("publicID"),
                             String.valueOf(comment_edittext.getText()));


            // Add
            list.add(new CommentsObject(PreferenceManager.getDefaultSharedPreferences(this).getString("nick", "Anonym"),
                    new SimpleDateFormat("d MMMM, yyyy").format(new Date()),
                    String.valueOf(comment_edittext.getText()),
                    CommentsAdapter.TYPE_ITEM));


            // Notify
            adapter.notifyDataSetChanged();


            // Hide keyboard and clear text field
            comment_edittext.setText("");
            comment_edittext.clearFocus();
            View viewtest = this.getCurrentFocus();
            if (viewtest != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(viewtest.getWindowToken(), 0);
            }


            // Scroll to bottom
            recyclerView.smoothScrollToPosition(list.size() - 1);



        } else {

            // Offline alert

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
