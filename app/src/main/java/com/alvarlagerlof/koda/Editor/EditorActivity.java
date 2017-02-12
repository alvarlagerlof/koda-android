package com.alvarlagerlof.koda.Editor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.alvarlagerlof.koda.Projects.ProjectsRealmObject;
import com.alvarlagerlof.koda.Projects.ProjectsSync;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.ViewPagerAdapter;

import io.realm.Realm;


public class EditorActivity extends AppCompatActivity {

    Toolbar toolbar;

    String private_id;
    String title;
    String code;

    EditorEditFragment editorEditFragment;
    EditorRunFragment editorRunFragment;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);

        // From intent
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            private_id = sharedPreferences.getString("privateID", "null");
            title = sharedPreferences.getString("title", "null");
        } else {
            private_id = extras.getString("privateID");
            title = extras.getString("title");

            SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("privateID", private_id);
            editor.putString("title", title);
            editor.apply();
        }


        // Get the code
        Realm realm = Realm.getDefaultInstance();
        ProjectsRealmObject object = realm.where(ProjectsRealmObject.class)
                .equalTo("privateId", private_id)
                .findFirst();

        realm.beginTransaction();
        code = object.getCode();
        realm.commitTransaction();


        // Set up the adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // 2D
        editorEditFragment = new EditorEditFragment();
        editorEditFragment.setCode(code);
        adapter.addFragment(editorEditFragment, "Redigera");

        // 3D
        editorRunFragment = new EditorRunFragment();
        editorRunFragment.setCode(code);
        editorRunFragment.setTitle(title);
        adapter.addFragment(editorRunFragment, "Kör");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        // On page change
        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {}

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}

            @Override
            public void onPageSelected(int pos) {
                switch (pos) {
                    case 0:
                        // Editor
                        editorRunFragment.clearWebView();

                        toolbar.getMenu().findItem(R.id.colorpicker).setVisible(true);
                        toolbar.getMenu().findItem(R.id.fontminus).setVisible(true);
                        toolbar.getMenu().findItem(R.id.fontplus).setVisible(true);
                        toolbar.getMenu().findItem(R.id.reload).setVisible(false);
                        break;
                    case 1:
                        // Run
                        editorRunFragment.setCode(editorEditFragment.getCode());
                        editorRunFragment.load();

                        toolbar.getMenu().findItem(R.id.colorpicker).setVisible(false);
                        toolbar.getMenu().findItem(R.id.fontminus).setVisible(false);
                        toolbar.getMenu().findItem(R.id.fontplus).setVisible(false);
                        toolbar.getMenu().findItem(R.id.reload).setVisible(true);

                        InputMethodManager inputMethodManager = (InputMethodManager) EditorActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(EditorActivity.this.getCurrentFocus().getWindowToken(), 0);

                        EditorSave saveTask = new EditorSave(EditorActivity.this, private_id, editorEditFragment.getCode());
                        saveTask.execute();
                }
            }
        };
        viewPager.setOnPageChangeListener(pageChangeListener);

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_editor, menu);
        toolbar.getMenu().findItem(R.id.reload).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.fontplus:
                editorEditFragment.changeFontSize(1);
                return true;

            case R.id.fontminus:
                editorEditFragment.changeFontSize(-1);
                return true;

            case R.id.reload:
                editorRunFragment.reload();
                return true;

            case R.id.colorpicker:
                editorEditFragment.chooseColor(EditorActivity.this, 0xffffffff);
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        new ProjectsSync(EditorActivity.this).execute();
    }
}
