package com.alvarlagerlof.koda.Login

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import com.alvarlagerlof.koda.Extensions.hideKeyboard
import com.alvarlagerlof.koda.Extensions.isConnected
import com.alvarlagerlof.koda.Extensions.showKeyboard
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Vars
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crash.FirebaseCrash
import kotlinx.android.synthetic.main.login_forgot_pass_activity.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * Created by alvar on 2016-07-02.
 */
class ForgotPasswordActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_forgot_pass_activity)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white)


        Glide.with(this)
                .load(Vars.URL_LOGIN_FORGOT_IMAGE)
                .into(background)

        background.setOnClickListener {
            background.hideKeyboard()
        }

        FirebaseAnalytics.getInstance(this).logEvent("login_forgot_open", Bundle())


    }


    @Suppress("UNUSED_PARAMETER")
    fun next(view: View) {
        email.hideKeyboard()
        if (this.isConnected()) {
            pacman_progress.visibility = View.VISIBLE
            PasswordAsync().execute()
        } else {
            AlertDialog.Builder(this)
                    .setTitle("Ingen ansluting")
                    .setMessage("Gå in i inställingar och se till att du har Wifi eller mobildata på")
                    .setPositiveButton("Ok") { dialog, _ -> dialog.cancel() }
                    .show()
        }



    }

    @Suppress("UNUSED_PARAMETER")
    fun finish(view: View) {
        background.hideKeyboard()
        finish()
    }


    internal inner class PasswordAsync : AsyncTask<Void, Int, String>() {

        var emailString: String = ""

        override fun onPreExecute() {
            super.onPreExecute()
            emailString = email.text.toString()
        }

        override fun doInBackground(vararg arg0: Void): String {

            val client = OkHttpClient.Builder().build()

            val formBody = FormBody.Builder()
                    .add("email", emailString)
                    .add("verification_reset", "7")
                    .add("headless", "thisIsSet")
                    .build()


            val request = Request.Builder()
                    .url(Vars.URL_LOGIN_FORGOT)
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

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)

            if (s != null) {
                try {

                    val jsonObject = JSONObject(s)

                    if (jsonObject.getString("success") == "true") {

                        FirebaseAnalytics.getInstance(applicationContext).logEvent("login_forgot_successful", Bundle())


                        result_text.text = jsonObject.getString("message")

                        val fadeOut = AlphaAnimation(1f, 0f)
                        fadeOut.interpolator = AccelerateInterpolator() //and this
                        fadeOut.duration = 300
                        fadeOut.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {

                            }

                            override fun onAnimationEnd(animation: Animation) {
                                email_view.visibility = View.INVISIBLE
                            }

                            override fun onAnimationRepeat(animation: Animation) {

                            }
                        })


                        val fadeIn = AlphaAnimation(0f, 1f)
                        fadeIn.interpolator = DecelerateInterpolator() //projects_add this
                        fadeIn.duration = 300
                        fadeIn.startOffset = 300
                        fadeIn.fillAfter = true


                        result_text.visibility = View.VISIBLE

                        email_view.startAnimation(fadeOut)
                        result_view.startAnimation(fadeIn)


                    } else {

                        FirebaseAnalytics.getInstance(applicationContext).logEvent("login_forgot_failed", Bundle())


                        error_text.text = jsonObject.getString("message")

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
