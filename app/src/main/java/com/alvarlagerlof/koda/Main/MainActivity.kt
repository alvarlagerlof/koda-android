package com.alvarlagerlof.koda.Main


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.alvarlagerlof.koda.Api.ApiFragment
import com.alvarlagerlof.koda.BuildConfig
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Extensions.replaceFragment
import com.alvarlagerlof.koda.Killswitch.KillswitchActivity
import com.alvarlagerlof.koda.Login.LoginSync
import com.alvarlagerlof.koda.MigrationRealm
import com.alvarlagerlof.koda.Projects.ProjectsFragment
import com.alvarlagerlof.koda.QrCodeShare.QrScanner
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Settings.SettingsActivity
import com.alvarlagerlof.koda.Vars
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.main_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by alvar on 2016-07-02.
 */
class MainActivity : AppCompatActivity() {


    internal val PERMISSION_REQUEST_USE_CAMERA = 0

    internal lateinit var fragment_projects: ProjectsFragment
    internal lateinit var fragment_api: ApiFragment
    //internal lateinit var fragment_guides: GuidesFragment
    //internal lateinit var fragment_archive: ArchiveFragment


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Projekt"

        setupRealmMigration()
        setupRemoteConfig()


        setupBottomBar()

        

        if (savedInstanceState == null) {
            fragment_container.replaceFragment(supportFragmentManager, fragment_projects)
        }





    }



    fun setupRealmMigration() {
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(1)
                .migration(MigrationRealm())
                .build())

        // Needs realm before run
        fragment_projects = ProjectsFragment()
        fragment_api = ApiFragment()

    }

    fun setupRemoteConfig() {
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig.setConfigSettings(FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build())
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults)
        firebaseRemoteConfig.fetch((if (firebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) 0 else 60).toLong())
                .addOnCompleteListener(this@MainActivity) { task ->
                    if (task.isSuccessful) {
                        firebaseRemoteConfig.activateFetched()

                        if (Vars.KILLSWITCH_ON) {
                            val intent = Intent(applicationContext, KillswitchActivity::class.java)
                            intent.putExtra("didNotFetch", false)
                            startActivity(intent)
                        } else {
                            LoginSync(applicationContext)
                        }

                    } else {

                        if (applicationContext.isConnected()) {
                            val intent = Intent(applicationContext, KillswitchActivity::class.java)
                            intent.putExtra("didNotFetch", true)
                            startActivity(intent)
                        } else {
                            LoginSync(applicationContext)
                        }

                    }
                }
    }

    fun setupBottomBar() {

        bottom_bar.setOnTabSelectListener { tabId ->
            when (tabId) {
                R.id.tab_projects -> {
                    fragment_container.replaceFragment(supportFragmentManager, fragment_projects)
                    toolbar.title = "Projekt"
                }
                R.id.tab_api -> {
                    fragment_api = ApiFragment()
                    fragment_container.replaceFragment(supportFragmentManager, fragment_api)
                    toolbar.title = "API"
                }
                /*R.id.tab_guides -> {
                    fragment_container.replaceFragment(supportFragmentManager, fragment_guides)
                    toolbar.title = "Guider"
                    setAppBarElevation(16f)
                }
                R.id.tab_archive -> {
                   fragment_container.replaceFragment(supportFragmentManager, fragment_archive)
                    toolbar.title = "Arkivet"
                    setAppBarElevation(0f)
                }*/
            }
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MainElevationEvent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Handler().postDelayed({
                appbar_layout.elevation = event.elevation
            }, 30)
        }
    }



    // Boring stuff
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.settings -> startActivity(Intent(this@MainActivity, SettingsActivity::class.java))

            R.id.scan -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this@MainActivity,
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                                android.Manifest.permission.CAMERA)) {

                            AlertDialog.Builder(this@MainActivity)
                                    .setTitle("Skanner")
                                    .setMessage("Skannern böehver tillstånd att använda kameran för att fungera.")
                                    .setPositiveButton("Ge tillstånd") { dialog, _ ->
                                        dialog.dismiss()
                                        ActivityCompat.requestPermissions(this@MainActivity,
                                                arrayOf(android.Manifest.permission.CAMERA),
                                                PERMISSION_REQUEST_USE_CAMERA)
                                    }
                                    .setNegativeButton("avbryt") { dialog, _ -> dialog.dismiss() }
                                    .show()


                        } else {
                            // No explanation needed, we can request the permission.
                            ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(android.Manifest.permission.CAMERA),
                                    PERMISSION_REQUEST_USE_CAMERA)

                        }
                    } else {
                        startActivity(Intent(this@MainActivity, QrScanner::class.java))
                    }
                } else {
                    startActivity(Intent(this@MainActivity, QrScanner::class.java))
                }

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_USE_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this@MainActivity, QrScanner::class.java))
                }
                return
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


}