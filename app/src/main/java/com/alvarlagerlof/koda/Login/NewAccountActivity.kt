package com.alvarlagerlof.koda.Login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import com.alvarlagerlof.koda.Cookies.PersistentCookieStore
import com.alvarlagerlof.koda.Extensions.hideKeyboard
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Extensions.showKeyboard
import com.alvarlagerlof.koda.Main.MainActivity
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Vars
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crash.FirebaseCrash
import kotlinx.android.synthetic.main.login_new_account_activity.*
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
class NewAccountActivity : AppCompatActivity() {


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_new_account_activity)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white)

        Glide.with(this)
                .load(Vars.URL_LOGIN_NEW_IMAGE)
                .into(background)

        background.setOnClickListener {
            it.hideKeyboard()
        }

        FirebaseAnalytics.getInstance(this).logEvent("login_create_open", Bundle())

    }


    fun createAccount(view: View) {
        view.hideKeyboard()

        if (this.isConnected()) {
            val createAccountAsync = createAccountAsync()
            createAccountAsync.execute()
        } else {
            AlertDialog.Builder(this)
                    .setTitle("Ingen ansluting")
                    .setMessage("Gå in i inställingar och se till att du har Wifi eller mobildata på")
                    .setPositiveButton("Ok") { dialog, _ -> dialog.cancel() }
                    .show()
        }


    }

    fun finish(view: View) {
        view.hideKeyboard()
        finish()
    }


    internal inner class createAccountAsync : AsyncTask<Void, Int, String>() {

        var emailString: String = ""

        override fun onPreExecute() {
            super.onPreExecute()
            emailString = email.text.toString()
            pacman_progress.visibility = View.VISIBLE
        }


        override fun doInBackground(vararg arg0: Void): String {


            val cookieHandler = CookieManager(
                    PersistentCookieStore(this@NewAccountActivity), CookiePolicy.ACCEPT_ALL)

            val client = OkHttpClient.Builder()
                    .cookieJar(JavaNetCookieJar(cookieHandler))
                    .build()

            val formBody = FormBody.Builder()
                    .add("email", emailString)
                    .add("verification", "8")
                    .add("headless", "yes")
                    .build()


            val request = Request.Builder()
                    .url(Vars.URL_LOGIN_NEW)
                    .post(formBody)
                    .build()

            var result: String? = null

            try {
                val response = client.newCall(request).execute()
                result = response.body()!!.string()
                response.body()!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }


            return result as String
        }


        @SuppressLint("ApplySharedPref")
        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)

            if (s != null) {
                try {

                    val jsonObject = JSONObject(s)

                    if (!jsonObject.has("error")) {

                        FirebaseAnalytics.getInstance(this@NewAccountActivity).logEvent("login_create_successful", Bundle())


                        username_text.append(jsonObject.getString("username"))
                        password_text.append(jsonObject.getString("password"))

                        PreferenceManager.getDefaultSharedPreferences(this@NewAccountActivity)
                                .edit()
                                .putString(Vars.PREF_EMAIL, jsonObject.getString("username"))
                                .putString(Vars.PREF_PASSWORD, jsonObject.getString("password"))
                                .commit()


                        val fadeOut = AlphaAnimation(1f, 0f)
                        fadeOut.interpolator = AccelerateInterpolator() //and this
                        fadeOut.duration = 300
                        fadeOut.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {}

                            override fun onAnimationEnd(animation: Animation) {
                                email_view.visibility = View.GONE
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })




                        email_view.startAnimation(fadeOut)

                        username_text.visibility = View.VISIBLE
                        password_text.visibility = View.VISIBLE


                        val fadeIn = AlphaAnimation(0f, 1f)
                        fadeIn.interpolator = DecelerateInterpolator() //projects_add this
                        fadeIn.duration = 300
                        fadeIn.startOffset = 300
                        fadeIn.fillAfter = true

                        result_view.visibility = View.VISIBLE
                        result_view.startAnimation(fadeIn)

                    } else {

                        FirebaseAnalytics.getInstance(applicationContext).logEvent("login_create_failed", Bundle())

                        error_text.text = jsonObject.getString("error")

                        val fadeOut = AlphaAnimation(1f, 0f)
                        fadeOut.interpolator = AccelerateInterpolator()
                        fadeOut.duration = 300
                        fadeOut.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {}

                            override fun onAnimationEnd(animation: Animation) {
                                email_view.visibility = View.GONE
                                result_view.visibility = View.GONE
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })



                        email_view.startAnimation(fadeOut)


                        val fadeIn = AlphaAnimation(0f, 1f)
                        fadeIn.interpolator = DecelerateInterpolator() //projects_add this
                        fadeIn.duration = 300
                        fadeIn.startOffset = 300
                        fadeIn.fillAfter = true

                        error_view.visibility = View.VISIBLE
                        error_view.startAnimation(fadeIn)


                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                    FirebaseCrash.report(e)
                }

            }


        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun reset(view: View) {

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator() //projects_add this
        fadeIn.duration = 300
        fadeIn.startOffset = 300
        fadeIn.fillAfter = true

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.duration = 300
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                email_view.visibility = View.VISIBLE
                pacman_progress.visibility = View.INVISIBLE
                email_view.startAnimation(fadeIn)
                error_view.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })


        error_view.startAnimation(fadeOut)

        email.showKeyboard()

    }


    @Suppress("UNUSED_PARAMETER")
    fun openLab(view: View) {

        // TOOD: SAVE THE USERNAME AND PASS

        startActivity(Intent(this, MainActivity::class.java))
        PreferenceManager.getDefaultSharedPreferences(this@NewAccountActivity)
                .edit()
                .putString(Vars.PREF_EMAIL, username_text.text.toString())
                .putString(Vars.PREF_PASSWORD, password_text.text.toString())
                .apply()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            android.R.id.home -> {
                background.hideKeyboard()
                startActivity(Intent(this, LoginActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

}
