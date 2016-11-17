package com.alvarlagerlof.koda.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.MainAcitivty;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

/**
 * Created by alvar on 2016-07-02.
 */
public class LoginActivity extends AppCompatActivity {

    TextInputEditText email;
    TextInputEditText password;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activty);

        email = (TextInputEditText) findViewById(R.id.email);
        password = (TextInputEditText) findViewById(R.id.password);


        Glide.with(LoginActivity.this)
                .load(PrefValues.URL_LOGIN_IMAGE)
                .into((ImageView) findViewById(R.id.background));

    }


    public void login(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email.getText().toString());
        editor.apply();

        new LoginAsync().execute();
    }

    public void createAccount(View view) {
        Intent intent = new Intent(this, NewAccountActivity.class);
        startActivity(intent);
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }





    class LoginAsync extends AsyncTask<Void, Integer, String> {

        String emailString;
        String passwordString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emailString = email.getText().toString();
            passwordString = password.getText().toString();

        }


        protected String doInBackground(Void...arg0) {

            String result = null;

            CookieHandler cookieHandler = new CookieManager(
                    new PersistentCookieStore(LoginActivity.this), CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", emailString)
                    .add("password", passwordString)
                    .add("headless", "thisIsHeadLess")
                    .build();

            Request request = new Request.Builder()
                    .url(PrefValues.URL_LOGIN)
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
                response.body().close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    if (new JSONObject(s).getString("access").equals("granted")) {

                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this)
                                .edit()
                                .putString(PrefValues.PREF_EMAIL, emailString)
                                .putString(PrefValues.PREF_PASSWORD, passwordString)
                                .commit();

                        startActivity(new Intent(LoginActivity.this, MainAcitivty.class));

                    } else {

                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Ops!")
                                .setMessage("Felaktigt skriven email eller lösenord")
                                .setPositiveButton("Försök igen", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }


}
