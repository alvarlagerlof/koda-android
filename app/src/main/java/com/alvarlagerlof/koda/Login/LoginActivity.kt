package com.alvarlagerlof.koda.Login

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Extensions.hideKeyboard
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Main.MainActivity
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Vars
import com.arasthel.asyncjob.AsyncJob
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import io.realm.Realm
import kotlinx.android.synthetic.main.login_activty.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.JavaNetCookieJar
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.CookieManager
import java.net.CookiePolicy


/**
 * Created by alvar on 2016-07-02.
 */
class LoginActivity : AppCompatActivity() {



    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activty)

        Glide.with(this@LoginActivity)
                .load(Vars.URL_LOGIN_IMAGE)
                .into(background)


        background.setOnClickListener {
            background.hideKeyboard()
        }

        FirebaseAnalytics.getInstance(this).logEvent("login_main_open", Bundle())


    }


    @Suppress("UNUSED_PARAMETER")
    fun login(view: View) {
        if (applicationContext.isConnected()) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = sharedPref.edit()
            editor.putString(Vars.PREF_EMAIL, email.text.toString())
            editor.putString(Vars.PREF_PASSWORD, password.text.toString())
            editor.apply()



            AsyncJob.AsyncJobBuilder<String>()
                    .doInBackground {
                        Realm.getDefaultInstance().beginTransaction()
                        Realm.getDefaultInstance().deleteAll()
                        Realm.getDefaultInstance().commitTransaction()

                        var result: String? = null

                        val formBody = FormBody.Builder()
                                .add("email", email.text.toString())
                                .add("password", password.text.toString())
                                .add("headless", "thisIsHeadLess")
                                .build()

                        val request = Request.Builder()
                                .url(Vars.URL_LOGIN)
                                .post(formBody)
                                .build()

                        try {
                            val response = OkHttpClient.Builder()
                                    .cookieJar(JavaNetCookieJar(CookieManager(PersistentCookieStore(applicationContext), CookiePolicy.ACCEPT_ALL)))
                                    .build().newCall(request).execute()

                            result = response.body()!!.string()
                            response.body()!!.close()

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }


                        result
                    }
                    .doWhenFinished { result ->
                        if (result != null) {
                            try {


                                if (JSONObject(result.toString()).getString("access") == "granted") {

                                    FirebaseAnalytics.getInstance(this).logEvent("login_main_successful", Bundle())


                                    PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                                            .edit()
                                            .putString(Vars.PREF_EMAIL, email.text.toString())
                                            .putString(Vars.PREF_PASSWORD, password.text.toString())
                                            .commit()

                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))

                                } else {

                                    FirebaseAnalytics.getInstance(this).logEvent("login_main_failed", Bundle())

                                    AlertDialog.Builder(this@LoginActivity)
                                            .setTitle("Ops!")
                                            .setMessage("Felaktigt skriven email eller lösenord")
                                            .setPositiveButton("Försök igen") { dialog, _ -> dialog.cancel() }
                                            .show()

                                }

                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }.create().start()

        } else {
            AlertDialog.Builder(this@LoginActivity)
                    .setTitle("Ingen ansluting")
                    .setMessage("Gå in i inställingar och se till att du har Wifi eller mobildata på")
                    .setPositiveButton("Ok") { dialog, _ -> dialog.cancel() }
                    .show()
        }


    }

    @Suppress("UNUSED_PARAMETER")
    fun createAccount(view: View) {
        if (applicationContext.isConnected()) {
            val intent = Intent(this, NewAccountActivity::class.java)
            startActivity(intent)

        } else {
            AlertDialog.Builder(this@LoginActivity)
                    .setTitle("Ingen ansluting")
                    .setMessage("Gå in i inställingar och se till att du har Wifi eller mobildata på")
                    .setPositiveButton("Ok") { dialog, _ -> dialog.cancel() }
                    .show()
        }
    }


    @Suppress("UNUSED_PARAMETER")
    fun forgotPassword(view: View) {
        if (applicationContext.isConnected()) {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)

        } else {
            AlertDialog.Builder(this@LoginActivity)
                    .setTitle("Ingen ansluting")
                    .setMessage("Gå in i inställingar och se till att du har Wifi eller mobildata på")
                    .setPositiveButton("Ok") { dialog, _ -> dialog.cancel() }
                    .show()
        }
    }

    override fun onBackPressed() {}

}
