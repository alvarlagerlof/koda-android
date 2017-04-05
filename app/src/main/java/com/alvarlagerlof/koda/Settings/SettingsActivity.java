package com.alvarlagerlof.koda.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.Login.LoginActivity;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.RemoteConfigValues;
import com.bumptech.glide.Glide;

import io.realm.Realm;


/**
 * Created by alvar on 2017-04-02.
 */

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    TextInputEditText nickName;

    FrameLayout notifications;
    CheckBox notificationsCheckbox;

    LinearLayout about;

    LinearLayout logout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Inställnigarr");
        toolbar.setNavigationIcon(R.drawable.ic_close);

        // Init stuff
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();


        // Views
        nickName = (TextInputEditText) findViewById(R.id.nick_name);
        notifications = (FrameLayout) findViewById(R.id.notifications);
        notificationsCheckbox = (AppCompatCheckBox) findViewById(R.id.notifications_checkbox);
        about = (LinearLayout) findViewById(R.id.about);
        logout = (LinearLayout) findViewById(R.id.logout);


        // Nickname
        nickName.setText(prefs.getString(RemoteConfigValues.PREF_NICK, ""));
        nickName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsUpdateNick nickTask = new SettingsUpdateNick(SettingsActivity.this, String.valueOf(nickName.getText()));
                nickTask.execute();
            }

        });


        // Notifications(getContext()
        notificationsCheckbox.setChecked(prefs.getBoolean("notifications", true));
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsCheckbox.toggle();
                editor.putBoolean(RemoteConfigValues.PREF_NOTIFICATIONS, notificationsCheckbox.isChecked());
            }
        });


        // About
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = SettingsActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.settings_about, null);

                TextView versionTextView = (TextView) view.findViewById(R.id.version);
                ImageView imageView = (ImageView) view.findViewById(R.id.icon);
                TextView descriptionTextView = (TextView) view.findViewById(R.id.description);

                String app_description = "Den här applikationen skapades 2016 som ett projekt för " +
                        "Internetfonden, internetfonden.se\n\n"+

                        "Internetfonden finansieras av " +
                        "Internetstiftelsen i Sverige, IIS, som jobbar med att driva på en " +
                        "positiv utveckling av Internet i Sverige.\n\n" +

                        "Målet med koda.nu är att göra det lättare och roligare att lära sig programmering.\n\n" +

                        "Apputvecklare: Alvar Lagerlöf\n" +
                        "Instagram: @alvarlagerlof\n" +
                        "Github: github.com/\n\n" +

                        "Ansvarig för koda.nu: Mikael Tylmad\n" +
                        "Epost: mikael@roboro.se";

                descriptionTextView.setText(app_description);


                PackageInfo pInfo = null;
                try {
                    pInfo = SettingsActivity.this.getPackageManager().getPackageInfo(SettingsActivity.this.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = pInfo.versionName;
                int versionCode = pInfo.versionCode;
                version = "Version " + version + " (" + String.valueOf(versionCode) + ")\nCC BY-SA 3.0";

                versionTextView.setText(version);

                Glide.with(SettingsActivity.this)
                        .load(R.mipmap.ic_launcher)
                        .into(imageView);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.alertDialog);
                builder.setView(view);
                builder.create();
                builder.show();

            }
        });

        // Log out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PersistentCookieStore(SettingsActivity.this).removeAll();

                editor.putString(RemoteConfigValues.PREF_NICK, null);
                editor.putString(RemoteConfigValues.PREF_EMAIL, null);
                editor.commit();


                Realm.getDefaultInstance().beginTransaction();
                Realm.getDefaultInstance().deleteAll();
                Realm.getDefaultInstance().commitTransaction();

                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }




}
