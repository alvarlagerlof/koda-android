package com.alvarlagerlof.koda.Play

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.StandardBottomSheetFragment
import com.alvarlagerlof.koda.WebClient
import kotlinx.android.synthetic.main.play_activity.*

class PlayActivity : AppCompatActivity() {

    internal var url: String = ""
    internal var title: String = ""
    internal var author: String = ""


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupScreen()
        setContentView(R.layout.play_activity)


        // From intent
        val sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val extras = intent.extras
        if (extras == null) {
            url = sharedPreferences.getString("url", "")
            title = sharedPreferences.getString("title", "")
            author = sharedPreferences.getString("author", "")

        } else {
            url = extras.getString("url")
            title = extras.getString("title")
            author = extras.getString("author")
            editor.putString("url", url).apply()
            editor.putString("title", title).apply()
            editor.putString("author", author).apply()
        }


        // Toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white)
        supportActionBar!!.title = if (title == "") "Spela" else title

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

    fun more() {
        val bottomSheetFragment = StandardBottomSheetFragment(url, title, author)
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    fun setupScreen() {
        val flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_play, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reload -> {
                load()
                return true
            }
            R.id.more -> {
                more()
                return true
            }
            android.R.id.home -> {
                finish()
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    @SuppressLint("NewApi")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        load()
    }


}
