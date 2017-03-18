package com.alvarlagerlof.koda.Login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alvarlagerlof.koda.Cookies.PersistentCookieStore;
import com.alvarlagerlof.koda.MainAcitivty;
import com.alvarlagerlof.koda.PrefValues;
import com.alvarlagerlof.koda.R;
import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;

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
public class NewAccountActivity extends AppCompatActivity {

    // emailView
    TextInputEditText email;
    ProgressBar progressBar;
    LinearLayout emailView;

    // resultView
    LinearLayout resultView;
    TextView usernameText;
    TextView passwordText;

    // errorView
    LinearLayout errorView;
    TextView errorText;


    Toolbar toolbar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_new_account_activity);

        email = (TextInputEditText) findViewById(R.id.email);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        emailView = (LinearLayout) findViewById(R.id.email_view);
        resultView = (LinearLayout) findViewById(R.id.result_view);

        usernameText = (TextView) findViewById(R.id.username_text);
        passwordText = (TextView) findViewById(R.id.password_text);

        errorView = (LinearLayout) findViewById(R.id.error_view);
        errorText = (TextView) findViewById(R.id.error);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);



        Glide.with(this)
                .load(PrefValues.URL_LOGIN_CREATE_IMAGE)
                .into((ImageView) findViewById(R.id.background));

        findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) NewAccountActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(NewAccountActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        });

    }


    public void createAccount(View view) {
        createAccountAsync createAccountAsync = new createAccountAsync();
        createAccountAsync.execute();
    }

    public void finish(View view) {
        finish();
    }


    class createAccountAsync extends AsyncTask<Void, Integer, String> {

        String emailString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emailString = email.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
        }


        protected String doInBackground(Void...arg0) {


            CookieHandler cookieHandler = new CookieManager(
                    new PersistentCookieStore(NewAccountActivity.this), CookiePolicy.ACCEPT_ALL);

            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieHandler))
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", emailString)
                    .add("verification", "8")
                    .add("headless", "thisIsSet")
                    .build();


            Request request = new Request.Builder()
                    .url(PrefValues.URL_LOGIN_CREATE)
                    .post(formBody)
                    .build();

            String result = null;

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

            if (s != null) {
                try {

                    JSONObject jsonObject = new JSONObject(s);

                    if (!jsonObject.has("error")) {
                        usernameText.append(jsonObject.getString("username"));
                        passwordText.append(jsonObject.getString("password"));

                        PreferenceManager.getDefaultSharedPreferences(NewAccountActivity.this)
                                .edit()
                                .putString(PrefValues.PREF_EMAIL, jsonObject.getString("username"))
                                .putString(PrefValues.PREF_PASSWORD, jsonObject.getString("password"))
                                .commit();


                        Animation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                        fadeOut.setDuration(500);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                emailView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });




                        emailView.startAnimation(fadeOut);

                        usernameText.setVisibility(View.VISIBLE);
                        passwordText.setVisibility(View.VISIBLE);


                        Animation fadeIn = new AlphaAnimation(0, 1);
                        fadeIn.setInterpolator(new DecelerateInterpolator()); //projects_add this
                        fadeIn.setDuration(500);
                        fadeIn.setStartOffset(500);
                        fadeIn.setFillAfter(true);

                        resultView.setVisibility(View.VISIBLE);
                        resultView.startAnimation(fadeIn);

                    } else {

                        errorText.setText(jsonObject.getString("error"));

                        Animation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setInterpolator(new AccelerateInterpolator());
                        fadeOut.setDuration(500);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                emailView.setVisibility(View.GONE);
                                resultView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });




                        emailView.startAnimation(fadeOut);



                        Animation fadeIn = new AlphaAnimation(0, 1);
                        fadeIn.setInterpolator(new DecelerateInterpolator()); //projects_add this
                        fadeIn.setDuration(500);
                        fadeIn.setStartOffset(500);
                        fadeIn.setFillAfter(true);

                        errorView.setVisibility(View.VISIBLE);
                        errorView.startAnimation(fadeIn);


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrash.report(e);
                }

            }


        }
    }


    public void reset(View view) {

        final Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //projects_add this
        fadeIn.setDuration(500);
        fadeIn.setStartOffset(500);
        fadeIn.setFillAfter(true);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                emailView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                emailView.startAnimation(fadeIn);
                errorView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });


        errorView.startAnimation(fadeOut);

    }

    public void openLab(View view) {


        // TOOD: SAVE THE USERNAME AND PASS

        startActivity(new Intent(this, MainAcitivty.class));
        PreferenceManager.getDefaultSharedPreferences(NewAccountActivity.this)
                .edit()
                .putString(PrefValues.PREF_EMAIL, String.valueOf(usernameText.getText()))
                .putString(PrefValues.PREF_PASSWORD,  String.valueOf(passwordText.getText()))
                .apply();

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


}
