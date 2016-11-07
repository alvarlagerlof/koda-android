package com.alvarlagerlof.koda.Login;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.alvarlagerlof.koda.R;
import com.bumptech.glide.Glide;

/**
 * Created by alvar on 2016-07-02.
 */
public class NewAccountActivity extends AppCompatActivity {

    TextInputEditText email;

    ImageView background;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new_account);

        email = (TextInputEditText) findViewById(R.id.email);
        background = (ImageView) findViewById(R.id.background);


        Glide.with(background.getContext())
                .load("https://hd.unsplash.com/photo-1429051883746-afd9d56fbdaf")
                .into(background);

    }


    public void createAccount(View view) {


        /*SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email.getText().toString());
        editor.commit();

        new LoginAsync().execute();*/

    }

    public void finish(View view) {
        finish();
    }

    /*class LoginAsync extends AsyncTask<Void, Integer, String> {

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
                    new PersistentCookieStore(NewAccountActivity.this), CookiePolicy.ACCEPT_ALL);

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

            Intent intent = new Intent(NewAccountActivity.this, MainAcitivty.class);
            startActivity(intent);
        }
    }*/


}
