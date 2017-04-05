package com.alvarlagerlof.koda.Main;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.alvarlagerlof.koda.Api.FragmentApi;
import com.alvarlagerlof.koda.BuildConfig;
import com.alvarlagerlof.koda.Login.KeepLoggedIn;
import com.alvarlagerlof.koda.Projects.ProjectsFragment;
import com.alvarlagerlof.koda.QrCodeShare.QrScanner;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Settings.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by alvar on 2016-07-02.
 */
public class MainAcitivty extends AppCompatActivity {

    LinearLayout fragment_container;
    Toolbar toolbar;
    AppBarLayout appBarLayout;

    public static final int PERMISSION_REQUEST_USE_CAMERA = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        setSupportActionBar(toolbar);



        // Do stuff
        new KeepLoggedIn(this).execute();

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();

        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        firebaseRemoteConfig.fetch(firebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled() ? 0 : 3600)
                .addOnCompleteListener(MainAcitivty.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            firebaseRemoteConfig.activateFetched();
                        }
                    }
                });

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(MainAcitivty.this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                //.migration(new MyMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);



        fragment_container = (LinearLayout) findViewById(R.id.fragment_container);


        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_projects:
                        ProjectsFragment fragmentMyProjects = new ProjectsFragment();
                        FragmentTransaction ftMyCreations = getSupportFragmentManager().beginTransaction();
                        ftMyCreations.replace(R.id.fragment_container, fragmentMyProjects);
                        ftMyCreations.addToBackStack(null);
                        ftMyCreations.commit();
                        toolbar.setTitle("Projekt");
                        setAppBarElevation(16f);

                        break;
                    case R.id.tab_api:
                        FragmentApi fragmentApi = new FragmentApi();
                        FragmentTransaction ftApi = getSupportFragmentManager().beginTransaction();
                        ftApi.replace(R.id.fragment_container, fragmentApi);
                        ftApi.addToBackStack(null);
                        ftApi.commit();
                        toolbar.setTitle("API");
                        setAppBarElevation(0f);
                        break;
                    /*case R.id.tab_guides:
                        GuidesFragment guidesFragment = new GuidesFragment();
                        FragmentTransaction ftGuides = getSupportFragmentManager().beginTransaction();
                        ftGuides.replace(R.id.fragment_container, guidesFragment);
                        ftGuides.addToBackStack(null);
                        ftGuides.commit();
                        toolbar.setTitle("Guider");
                        setAppBarElevation(16f);

                        break;
                    case R.id.tab_archive:
                        ArchiveFragment fragmentArchive = new ArchiveFragment();
                        FragmentTransaction ftArchive = getSupportFragmentManager().beginTransaction();
                        ftArchive.replace(R.id.fragment_container, fragmentArchive);
                        ftArchive.addToBackStack(null);
                        ftArchive.commit();
                        toolbar.setTitle("Arkivet");
                        setAppBarElevation(0f);
                        break;*/


                }
            }
        });

        if (savedInstanceState == null) {
            ProjectsFragment fragmentMyCreations = new ProjectsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, fragmentMyCreations).commit();
        }

    }


    void setAppBarElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setElevation(elevation);
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
            case R.id.settings:
                startActivity(new Intent(MainAcitivty.this, SettingsActivity.class));

                break;

            case R.id.scan:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(MainAcitivty.this,
                            android.Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainAcitivty.this,
                                android.Manifest.permission.CAMERA)) {

                            new AlertDialog.Builder(MainAcitivty.this)
                                    .setTitle("Skanner")
                                    .setMessage("Skannern böehver tillstånd att använda kameran för att fungera.")
                                    .setPositiveButton("Ge tillstånd", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            ActivityCompat.requestPermissions(MainAcitivty.this,
                                                    new String[]{android.Manifest.permission.CAMERA},
                                                    PERMISSION_REQUEST_USE_CAMERA);
                                        }
                                    })
                                    .setNegativeButton("avbryt", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();


                        } else {
                            // No explanation needed, we can request the permission.

                            ActivityCompat.requestPermissions(MainAcitivty.this,
                                    new String[]{android.Manifest.permission.CAMERA},
                                    PERMISSION_REQUEST_USE_CAMERA);

                        }
                    } else {



                        startActivity(new Intent(MainAcitivty.this, QrScanner.class));
                    }
                } else {
                    startActivity(new Intent(MainAcitivty.this, QrScanner.class));
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_USE_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                    startActivity(new Intent(MainAcitivty.this, QrScanner.class));
                }
                return;
            }

            // other 'case' statements for other permssions
        }
    }

}
