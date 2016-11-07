package com.alvarlagerlof.koda.Settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.Login.LoginActivity;
import com.alvarlagerlof.koda.R;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-07-02.
 */
public class SettingsFragment extends Fragment {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    TextInputEditText nickName;

    FrameLayout notifications;
    CheckBox notificationsCheckbox;

    LinearLayout logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Init stuff
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = prefs.edit();


        // Views
        nickName = (TextInputEditText) view.findViewById(R.id.nick_name);
        notifications = (FrameLayout) view.findViewById(R.id.notifications);
        notificationsCheckbox = (AppCompatCheckBox) view.findViewById(R.id.notifications_checkbox);
        logout = (LinearLayout) view.findViewById(R.id.logout);


        // Nickname
        nickName.setText(prefs.getString("nick", ""));
        nickName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateNickName updateNickName = new updateNickName();
                updateNickName.execute(String.valueOf(nickName.getText()));
            }

        });


        // Notifications
        notificationsCheckbox.setChecked(prefs.getBoolean("notifications", true));
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsCheckbox.toggle();
                editor.putBoolean("notifications", notificationsCheckbox.isChecked());
            }
        });


        // Log out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PersistentCookieStore(getContext()).removeAll();

                editor.putString("nick", null);
                editor.putString("email", null);
                editor.commit();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    class updateNickName extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            CookieHandler cookieHandler = new CookieManager(
                    new PersistentCookieStore(getContext()), CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .build();

            FormBody data = new FormBody.Builder()
                    .add("nick", params[0])
                    .build();

            Request request = new Request.Builder()
                    .url("http://koda.nu/labbet")
                    .post(data)
                    .build();


            try {
                Response response = client.newCall(request).execute();
                Log.d("response", response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }


}
