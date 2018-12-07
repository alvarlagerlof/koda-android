package com.alvarlagerlof.koda.Play

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.WebClient
import kotlinx.android.synthetic.main.play_fullscreen_activity.*

class FullscreenPlay : AppCompatActivity() {


    internal lateinit var url: String
    internal lateinit var title: String


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpFullscreen()
        setContentView(R.layout.play_fullscreen_activity)


        // From intent
        val extras = intent.extras
        if (extras == null) {
            val sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
            url = sharedPreferences.getString("url", "")
            title = sharedPreferences.getString("title", "")
        } else {
            url = extras.getString("url")
            title = extras.getString("title")
            val sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("url", url).apply()
            editor.putString("title", title).apply()
        }


        load()

    }

    fun load() {
        web_view.settings.javaScriptEnabled = true
        web_view.settings.loadWithOverviewMode = true
        web_view.settings.useWideViewPort = true
        web_view.loadUrl(url)
        web_view.setOnLongClickListener { true }
        web_view.isLongClickable = false
        web_view.isHapticFeedbackEnabled = false
        web_view.webChromeClient = WebClient(this, title)
        web_view.setLayerType(if (Build.VERSION.SDK_INT >= 19) View.LAYER_TYPE_HARDWARE else View.LAYER_TYPE_SOFTWARE, null)
    }

    fun setUpFullscreen() {
        val flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        // This work only for android 4.4+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            window.decorView.systemUiVisibility = flags

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            val decorView = window.decorView
            decorView
                    .setOnSystemUiVisibilityChangeListener { visibility ->
                        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                decorView.systemUiVisibility = flags
                            }
                        }
                    }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    @SuppressLint("NewApi")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        web_view.reload()
    }
    
    
    

    
}
