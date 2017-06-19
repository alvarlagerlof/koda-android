package com.alvarlagerlof.koda.Settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Login.LoginActivity
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Settings.About.AboutActivity
import com.alvarlagerlof.koda.Vars
import io.realm.Realm
import kotlinx.android.synthetic.main.settings_activity.*


/**
 * Created by alvar on 2017-04-02.
 */

class SettingsActivity : AppCompatActivity() {

    internal lateinit var prefs: SharedPreferences
    internal lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)


        // Setup toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white)

        // Setup preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()


        setupNickname()
        setUpNotifications()
        setupAbout()
        setupLogout()


    }


    fun setupNickname() {
        nick_name.setText(prefs.getString(Vars.PREF_NICK, ""))
        nick_name.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val nickTask = SettingsUpdateNick(this@SettingsActivity, nick_name.text.toString())
                nickTask.execute()
            }

        })
    }

    fun setUpNotifications() {
        notifications_checkbox.isChecked = prefs.getBoolean("notifications", true)
        notifications.setOnClickListener {
            notifications_checkbox.toggle()
            editor.putBoolean(Vars.PREF_NOTIFICATIONS, notifications_checkbox.isChecked)
        }
    }

    fun setupAbout() {
        about.setOnClickListener {
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }

    fun setupLogout() {
        logout.setOnClickListener {
            PersistentCookieStore(this@SettingsActivity).removeAll()

            editor.putString(Vars.PREF_NICK, null)
            editor.putString(Vars.PREF_EMAIL, null)
            editor.commit()


            Realm.getDefaultInstance().beginTransaction()
            Realm.getDefaultInstance().deleteAll()
            Realm.getDefaultInstance().commitTransaction()

            finish()

            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}
