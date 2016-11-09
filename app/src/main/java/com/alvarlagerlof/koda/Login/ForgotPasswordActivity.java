package com.alvarlagerlof.koda.Login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alvarlagerlof.koda.R;
import com.bumptech.glide.Glide;

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

    TextView resultTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_forgot_pass_activity);

        email = (TextInputEditText) findViewById(R.id.email);
        nextButton = (Button) findViewById(R.id.next_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        emailView = (LinearLayout) findViewById(R.id.email_view);
        resultView = (LinearLayout) findViewById(R.id.result_view);

        resultTextView = (TextView) findViewById(R.id.result_text);


        Glide.with(this)
                .load("https://hd.unsplash.com/photo-1461632830798-3adb3034e4c8")
                .into((ImageView) findViewById(R.id.background));

    }



    public void finish(View view) {
        finish();
    }

    public void next(View view) {
        // TODO: CHECK IF EMAIL IS VALID

        nextButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new PasswordAsync().execute();
    }




    class PasswordAsync extends AsyncTask<Void, Integer, String> {

        String emailString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            emailString = email.getText().toString();
        }


        protected String doInBackground(Void...arg0) {

            OkHttpClient client = new OkHttpClient.Builder().build();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", emailString)
                    .add("verification_reset", "7")
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

            resultTextView.setText("Ett återställningsmail har skickats till\n" + emailString);

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

        }
    }


}
