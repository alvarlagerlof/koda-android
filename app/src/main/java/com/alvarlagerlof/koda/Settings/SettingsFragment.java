package com.alvarlagerlof.koda.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.Login.LoginActivity;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;
import com.bumptech.glide.Glide;

import io.realm.Realm;


/**
 * Created by alvar on 2016-07-02.
 */
public class SettingsFragment extends Fragment {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    TextInputEditText nickName;

    FrameLayout notifications;
    CheckBox notificationsCheckbox;

    LinearLayout about;

    LinearLayout logout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        // Init stuff
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();


        // Views
        nickName = (TextInputEditText) view.findViewById(R.id.nick_name);
        notifications = (FrameLayout) view.findViewById(R.id.notifications);
        notificationsCheckbox = (AppCompatCheckBox) view.findViewById(R.id.notifications_checkbox);
        about = (LinearLayout) view.findViewById(R.id.about);
        logout = (LinearLayout) view.findViewById(R.id.logout);


        // Nickname
        nickName.setText(prefs.getString(PrefValues.PREF_NICK, ""));
        nickName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SettingsUpdateNick nickTask = new SettingsUpdateNick(getContext(), String.valueOf(nickName.getText()));
                nickTask.execute();
            }

        });


        // Notifications
        notificationsCheckbox.setChecked(prefs.getBoolean("notifications", true));
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsCheckbox.toggle();
                editor.putBoolean(PrefValues.PREF_NOTIFICATIONS, notificationsCheckbox.isChecked());
            }
        });


        // About
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
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
                    pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String version = pInfo.versionName;
                int versionCode = pInfo.versionCode;
                version = "Version " + version + " (" + String.valueOf(versionCode) + ")\nCC BY-SA 3.0";

                versionTextView.setText(version);

                Glide.with(getContext())
                        .load(R.mipmap.ic_launcher)
                        .into(imageView);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialog);
                builder.setView(view);
                builder.create();
                builder.show();

            }
        });

        // Log out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PersistentCookieStore(getContext()).removeAll();

                editor.putString(PrefValues.PREF_NICK, null);
                editor.putString(PrefValues.PREF_EMAIL, null);
                editor.commit();


                Realm.getDefaultInstance().beginTransaction();
                Realm.getDefaultInstance().deleteAll();
                Realm.getDefaultInstance().commitTransaction();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }





}
