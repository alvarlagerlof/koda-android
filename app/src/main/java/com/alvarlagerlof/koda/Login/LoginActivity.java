package com.alvarlagerlof.koda.Login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.MainAcitivty;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.Utils.ConnectionUtils;
import com.arasthel.asyncjob.AsyncJob;
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

import static com.alvarlagerlof.koda.PrefValues.PREF_EMAIL;
import static com.alvarlagerlof.koda.PrefValues.PREF_PASSWORD;

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



        findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) LoginActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        });

    }


    public void login(View view) {
        if (ConnectionUtils.isConnected(this)) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(PREF_EMAIL, email.getText().toString());
            editor.putString(PREF_PASSWORD, password.getText().toString());
            editor.apply();



            new AsyncJob.AsyncJobBuilder<String>()
                    .doInBackground(new AsyncJob.AsyncAction<String>() {
                        @Override
                        public String doAsync() {

                            String result = null;

                            Log.d("emailtext", password.getText().toString());

                            RequestBody formBody = new FormBody.Builder()
                                    .add("email", email.getText().toString())
                                    .add("password", password.getText().toString())
                                    .add("headless", "thisIsHeadLess")
                                    .build();

                            Request request = new Request.Builder()
                                    .url(PrefValues.URL_LOGIN)
                                    .post(formBody)
                                    .build();

                            try {
                                Response response = new OkHttpClient.Builder()
                                        .cookieJar(new JavaNetCookieJar(new CookieManager(new PersistentCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL)))
                                        .build().newCall(request).execute();

                                result = response.body().string();
                                response.body().close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            return result;
                        }
                    })
                    .doWhenFinished(new AsyncJob.AsyncResultAction<String>() {
                        @SuppressLint("ApplySharedPref")
                        @Override
                        public void onResult(String result) {
                            if (result != null) {
                                try {

                                    if (new JSONObject(result).getString("access").equals("granted")) {

                                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this)
                                                .edit()
                                                .putString(PrefValues.PREF_EMAIL, email.getText().toString())
                                                .putString(PREF_PASSWORD, password.getText().toString())
                                                .commit();

                                        startActivity(new Intent(LoginActivity.this, MainAcitivty.class));

                                    } else {

                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("Ops!")
                                                .setMessage("Felaktigt skriven email eller lösenord "+ new JSONObject(result).toString(2))
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

                    }).create().start();

        } else {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Ingen ansluting")
                    .setMessage("Gå in i inställingar och se till att du har Wifi eller mobildata på")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }


    }

    public void createAccount(View view) {
        if (ConnectionUtils.isConnected(this)) {
            Intent intent = new Intent(this, NewAccountActivity.class);
            startActivity(intent);

        } else {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Ingen ansluting")
                    .setMessage("Gå in i inställingar och se till att du har Wifi eller mobildata på")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    public void forgotPassword(View view) {
        if (ConnectionUtils.isConnected(this)) {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);

        } else {
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Ingen ansluting")
                    .setMessage("Gå in i inställingar och se till att du har Wifi eller mobildata på")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }





    private class LoginAsync extends AsyncTask<Void, Void, String> {

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

            CookieHandler cookieHandler = new CookieManager(new PersistentCookieStore(LoginActivity.this), CookiePolicy.ACCEPT_ALL);


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

        @SuppressLint("ApplySharedPref")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);





        }
    }


}
