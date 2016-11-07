package com.alvarlagerlof.koda.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.MainAcitivty;
import com.alvarlagerlof.koda.R;
import com.bumptech.glide.Glide;

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
    TextView signUp;

    ImageView background;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (TextInputEditText) findViewById(R.id.email);
        password = (TextInputEditText) findViewById(R.id.password);
        signUp = (TextView) findViewById(R.id.sign_up);
        background = (ImageView) findViewById(R.id.background);

        signUp.setText(Html.fromHtml("Har du inget konto? <b>Skapa ett!</b>"));


        Glide.with(background.getContext())
                .load("https://hd.unsplash.com/photo-1461749280684-dccba630e2f6")
                .into(background);

    }


    public void login(View view) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email.getText().toString());
        editor.commit();

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

            CookieHandler cookieHandler = new CookieManager(
                    new PersistentCookieStore(LoginActivity.this), CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", emailString)
                    .add("password", passwordString)
                    .build();


            Request request = new Request.Builder()
                    .url("https://koda.nu/login")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.d("response", response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }


            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent intent = new Intent(LoginActivity.this, MainAcitivty.class);
            startActivity(intent);
        }
    }


}