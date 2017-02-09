package com.alvarlagerlof.koda;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.alvarlagerlof.koda.Api.FragmentApi;
import com.alvarlagerlof.koda.Login.KeepLoggedIn;
import com.alvarlagerlof.koda.Projects.ProjectsFragment;
import com.alvarlagerlof.koda.Settings.SettingsFragment;
import com.alvarlagerlof.koda.Utils.DateConversionUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by alvar on 2016-07-02.
 */
public class MainAcitivty extends AppCompatActivity {



    LinearLayout fragment_container;

    public static ProjectsFragment fragmentMyProjects;

    Toolbar toolbar;
    AppBarLayout appBarLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        setSupportActionBar(toolbar);


        // Login
        new KeepLoggedIn(this).execute();


        // Realm
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);


        fragment_container = (LinearLayout) findViewById(R.id.fragment_container);



        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_projects:
                        fragmentMyProjects = new ProjectsFragment();
                        FragmentTransaction ftMyCreations = getSupportFragmentManager().beginTransaction();
                        ftMyCreations.replace(R.id.fragment_container, fragmentMyProjects);
                        ftMyCreations.addToBackStack(null);
                        ftMyCreations.commit();
                        toolbar.setTitle("Projekt");
                        appBarLayout.setElevation(16f);

                        break;
                    case R.id.tab_api:
                        FragmentApi fragmentApi = new FragmentApi();
                        FragmentTransaction ftApi = getSupportFragmentManager().beginTransaction();
                        ftApi.replace(R.id.fragment_container, fragmentApi);
                        ftApi.addToBackStack(null);
                        ftApi.commit();
                        toolbar.setTitle("API");
                        appBarLayout.setElevation(0f);
                        break;
                    /*case R.id.tab_guides:
                        GuidesFragment guidesFragment = new GuidesFragment();
                        FragmentTransaction ftGuides = getSupportFragmentManager().beginTransaction();
                        ftGuides.replace(R.id.fragment_container, guidesFragment);
                        ftGuides.addToBackStack(null);
                        ftGuides.commit();
                        toolbar.setTitle("Guider");
                        appBarLayout.setElevation(16f);

                        break;
                    case R.id.tab_archive:
                        ArchiveFragment fragmentArchive = new ArchiveFragment();
                        FragmentTransaction ftArchive = getSupportFragmentManager().beginTransaction();
                        ftArchive.replace(R.id.fragment_container, fragmentArchive);
                        ftArchive.addToBackStack(null);
                        ftArchive.commit();
                        toolbar.setTitle("Arkivet");
                        appBarLayout.setElevation(0f);
                        break;*/
                    case R.id.tab_settings:
                        SettingsFragment settingsFragment = new SettingsFragment();
                        FragmentTransaction ftSettings = getSupportFragmentManager().beginTransaction();
                        ftSettings.replace(R.id.fragment_container, settingsFragment);
                        ftSettings.addToBackStack(null);
                        ftSettings.commit();
                        toolbar.setTitle("Inställningar");
                        appBarLayout.setElevation(16f);
                        break;

                }
            }
        });

        if (savedInstanceState == null) {
            ProjectsFragment fragmentMyCreations = new ProjectsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, fragmentMyCreations).commit();
        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String url = String.valueOf(result.getContents());

                Intent intent = new Intent(MainAcitivty.this, PlayActivity.class);
                intent.putExtra("title", "");
                intent.putExtra("public_id", url.substring(url.lastIndexOf("/") + 1).trim());
                startActivity(intent);

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.scan:
                IntentIntegrator integrator = new IntentIntegrator(MainAcitivty.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setCameraId(0);
                integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(false);
                integrator.initiateScan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
