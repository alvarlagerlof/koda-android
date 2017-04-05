package com.alvarlagerlof.koda.Login;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alvarlagerlof.koda.R;
import com.alvarlagerlof.koda.RemoteConfigValues;
import com.bumptech.glide.Glide;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by alvar on 2016-07-02.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputEditText email;
    Button nextButton;
    ProgressBar progressBar;


    LinearLayout emailView;
    LinearLayout resultView;
    LinearLayout errorView;

    TextView resultTextView;
    TextView errorText;

    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_forgot_pass_activity);

        email = (TextInputEditText) findViewById(R.id.email);
        nextButton = (Button) findViewById(R.id.next_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        emailView = (LinearLayout) findViewById(R.id.email_view);
        resultView = (LinearLayout) findViewById(R.id.result_view);
        errorView = (LinearLayout) findViewById(R.id.error_view);


        resultTextView = (TextView) findViewById(R.id.result_text);
        errorText = (TextView) findViewById(R.id.error);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);



        Glide.with(this)
                .load(RemoteConfigValues.URL_LOGIN_FORGOT_IMAGE)
                .into((ImageView) findViewById(R.id.background));

        findViewById(R.id.background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) ForgotPasswordActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.background).getWindowToken(), 0);
            }
        });


    }



    public void next(View view) {
        progressBar.setVisibility(View.VISIBLE);
        new PasswordAsync().execute();

    }

    public void finish(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) ForgotPasswordActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.background).getWindowToken(), 0);
        finish();
    }


    class PasswordAsync extends AsyncTask<Void, Integer, String> {

        String emailString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emailString = email.getText().toString();
        }


        protected String doInBackground(Void... arg0) {

            OkHttpClient client = new OkHttpClient.Builder().build();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", emailString)
                    .add("verification_reset", "7")
                    .add("headless", "thisIsSet")
                    .build();


            Request request = new Request.Builder()
                    .url(RemoteConfigValues.URL_LOGIN_FORGOT)
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {

                    JSONObject jsonObject = new JSONObject(s);

                    if (jsonObject.getString("success").equals("true")) {

                        resultTextView.setText(jsonObject.getString("message"));

                        Animation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                        fadeOut.setDuration(500);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                emailView.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });


                        Animation fadeIn = new AlphaAnimation(0, 1);
                        fadeIn.setInterpolator(new DecelerateInterpolator()); //projects_add this
                        fadeIn.setDuration(500);
                        fadeIn.setStartOffset(1000);
                        fadeIn.setFillAfter(true);


                        resultTextView.setVisibility(View.VISIBLE);

                        emailView.startAnimation(fadeOut);
                        resultView.startAnimation(fadeIn);


                    } else {
                        errorText.setText(jsonObject.getString("message"));

                        Animation fadeOut = new AlphaAnimation(1, 0);
                        fadeOut.setInterpolator(new AccelerateInterpolator());
                        fadeOut.setDuration(500);
                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                emailView.setVisibility(View.GONE);
                                resultView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                InputMethodManager inputMethodManager = (InputMethodManager) ForgotPasswordActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.background).getWindowToken(), 0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
