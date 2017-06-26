package com.alvarlagerlof.koda.Editor

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.alvarlagerlof.koda.Extensions.*
import com.alvarlagerlof.koda.Projects.ProjectsRealmObject
import com.alvarlagerlof.koda.Projects.ProjectsSync
import com.alvarlagerlof.koda.Projects.ProjectsSyncEvent
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Vars
import com.alvarlagerlof.koda.WebClient
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import io.realm.Realm
import kotlinx.android.synthetic.main.editor_activity.*
import kotlinx.android.synthetic.main.editor_more_special_chars.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.padding
import org.jetbrains.anko.sdk25.coroutines.onClick


class EditorActivity : AppCompatActivity() {


    internal lateinit var realm: Realm
    internal lateinit var project: ProjectsRealmObject

    lateinit var firstPrivateID: String
    var syncedPrivateID: String? = null


    internal var codeSize = 15


    internal var VIEW_EDITOR = 0
    internal var VIEW_RESULT = 1
    internal var currentView = VIEW_EDITOR


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editor_activity)

        FirebaseAnalytics.getInstance(this).logEvent("projects_edit_open", Bundle())


        // From intent
        if (intent.extras != null) {
            firstPrivateID = intent.extras.getString("privateID")
            if (!firstPrivateID.contains("ny_ny_")) {
                syncedPrivateID = firstPrivateID
            }
        }


        // Code size
        codeSize = PreferenceManager.getDefaultSharedPreferences(applicationContext).getString(Vars.PREF_FONTSIZE, "15").toInt()
        setFontSize(codeSize)
        editor.typeface = Typeface.createFromAsset(applicationContext.assets, "SourceCodePro-Regular.ttf")

        // Toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white)

        // Get realm project
        realm = Realm.getDefaultInstance()
        getProject(setText = true)



        // Tabs
        tabs.addTab(tabs.newTab().setText("Redigera"))
        tabs.addTab(tabs.newTab().setText("Kör"))

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tabs.selectedTabPosition) {
                    0 -> setCurrentView(VIEW_EDITOR)
                    1 -> setCurrentView(VIEW_RESULT)
                }
            }
        })

        setUpSpecialCharsBar()



    }


    fun setCurrentView(view: Int) {
        currentView = view

        when (currentView) {
            VIEW_EDITOR -> showEditor()
            VIEW_RESULT -> showResult()
        }
    }




    fun showEditor() {

        FirebaseAnalytics.getInstance(this).logEvent("projects_edit_editor", Bundle())


        // Fix views
        editor_container.visibility = View.VISIBLE
        web_view_container.visibility = View.GONE
        loading_container.visibility = View.GONE
        special_chars.visibility = View.VISIBLE


        // Stop webiew
        web_view.pauseTimers()
        web_view.onPause()

        // Fix Toolbar
        toolbar.menu.show(R.id.colorpicker)
        toolbar.menu.show(R.id.fontminus)
        toolbar.menu.show(R.id.fontplus)
        toolbar.menu.hide(R.id.reload)


    }

    fun showResult() {

        FirebaseAnalytics.getInstance(this).logEvent("projects_edit_play", Bundle())

        // Hide keyboard
        editor.hideKeyboard()

        Handler().postDelayed({
            // Fix views
            web_view_container.visibility = View.VISIBLE
            editor_container.visibility = View.GONE
            loading_container.visibility = View.GONE
            special_chars.visibility = View.GONE

            // Load
            loadResult()
        }, 50)



        // Fix Toolbar
        toolbar.menu.hide(R.id.colorpicker)
        toolbar.menu.hide(R.id.fontminus)
        toolbar.menu.hide(R.id.fontplus)
        toolbar.menu.show(R.id.reload)


        saveCode()

    }



    fun loadResult() {
        web_view.onPause()
        web_view.onResume()
        web_view.resumeTimers()
        web_view.settings.javaScriptEnabled = true
        web_view.settings.loadWithOverviewMode = true
        web_view.settings.useWideViewPort = true
        web_view.setOnLongClickListener({ true })
        web_view.isLongClickable = false
        web_view.isHapticFeedbackEnabled = false
        web_view.webChromeClient = WebClient(this, project.title)
        web_view.setLayerType(if (Build.VERSION.SDK_INT >= 19) View.LAYER_TYPE_HARDWARE else View.LAYER_TYPE_SOFTWARE, null)
        web_view.loadDataWithBaseURL("file:///android_asset/", editor.cleanText, "text/html", "UTF-8", null)
    }


    fun setFontSize(newSize: Int) {
        codeSize = newSize.constrain(min = 5, max = 50)
        editor.textSize = codeSize.toFloat()

        PreferenceManager.getDefaultSharedPreferences(this).edit {
            putString(Vars.PREF_FONTSIZE, codeSize.toString())
        }
    }

    fun chooseColor(context: android.content.Context, initColor: Int) {
        ColorPickerDialogBuilder
                .with(context)
                .setTitle("Välj färg")
                .initialColor(initColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener { }
                .setPositiveButton("ok") { _, selectedColor, _ -> editor.text.insert(editor.selectionStart, String.format("#%06X", 0xFFFFFF and selectedColor)) }
                .setNegativeButton("avbryt") { _, _ -> }
                .build()
                .show()

    }


    fun setUpSpecialCharsBar() {
        val special_chars_list = listOf("( )", "{ }", "[ ]", ";", "\" \"",
                                        "-", "+", "*", "/",
                                        "=", "!",
                                        "%", "?",
                                        "&", "|",
                                        "<",  ">",
                                        ":")
        val lastVisible = 5
        var overflowItems: MutableList<String> = mutableListOf()

        for ((index, value) in special_chars_list.withIndex()) {

            // Left, top, right, bottom

            // Visible ones
            if (index < lastVisible) {
                val button = Button(ContextThemeWrapper(this, R.style.EditorBarButton), null, 0)

                val layoutParams = LinearLayout.LayoutParams(0.dp(this), 40.dp(this))
                layoutParams.weight = 1f
                layoutParams.setMargins(4.dp(this),
                        4.dp(this),
                        0.dp(this),
                        4.dp(this))

                button.layoutParams = layoutParams
                button.text = value
                button.onClick {
                    var text = button.text.toString().replace(" ", "")
                    editor.text.insert(editor.selectionStart, text)
                    if (text.length == 2) { editor.setSelection(editor.selectionStart-1) }
                }

                special_chars.addView(button)
            } else {
                if (index == lastVisible) {

                    // Add thee dots
                    val button = ImageButton(ContextThemeWrapper(this, R.style.EditorBarButton), null, 0)

                    val layoutParams = LinearLayout.LayoutParams(0.dp(this), 40.dp(this))
                    layoutParams.weight = 1f
                    layoutParams.setMargins(4.dp(this),
                            4.dp(this),
                            4.dp(this),
                            4.dp(this))

                    button.layoutParams = layoutParams
                    button.setImageDrawable(resources.getDrawable(R.drawable.ic_more_horiz))
                    button.padding = 5.dp(this)

                    button.onClick { showMoreSpecialChars(overflowItems) }

                    special_chars.addView(button)
                }

                overflowItems.add(value)

            }
        }


    }

    fun showMoreSpecialChars(overflowItems: MutableList<String>) {

        var dialog: AlertDialog? = null

        val view = LayoutInflater.from(this).inflate(R.layout.editor_more_special_chars, null)

        var tableRow: TableRow? = null

        for ((index, value) in overflowItems.withIndex()) {
            if (index.even()) {
                // Reset tableRow (if even #)
                tableRow = TableRow(this)
                tableRow.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
            }
            // Add button
            val button = Button(ContextThemeWrapper(this, R.style.EditorBarButton), null, 0)
            val layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT)

            layoutParams.setMargins(0.dp(this),
                    0.dp(this),
                    if (index.even()) 8.dp(this) else 0,
                    if (index != overflowItems.size) 8.dp(this) else 0)

            button.layoutParams = layoutParams
            button.setPadding(0, 10.dp(this), 0, 10.dp(this))
            button.text = value
            button.onClick {
                dialog?.dismiss()
                editor.text.insert(editor.selectionStart, button.text)
            }
            tableRow?.addView(button)

            if (index.odd() || index == overflowItems.size - 1) {
                // Add tableRow to tableView (if odd # or last)
                view.table_layout.addView(tableRow, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))
            }
        }


        dialog = AlertDialog.Builder(this)
                .setTitle("Fler specialtecken")
                .setView(view)
                .setPositiveButton("stäng") { dialog, _ -> dialog.cancel() }
                .show()

    }






    fun saveCode() {

        if (syncedPrivateID != null || !this.isConnected()) {

            project = Realm.getDefaultInstance().where(ProjectsRealmObject::class.java).equalTo("privateID", syncedPrivateID).findFirst()

            realm.executeTransaction { _ ->
                project.code = editor.cleanText
                project.updatedRealm = (System.currentTimeMillis() / 1000L).toString()
                project.synced = false
            }

            ProjectsSync(this@EditorActivity)

        }

    }


    fun getProject(setText: Boolean) {
        project = realm.where(ProjectsRealmObject::class.java).equalTo("privateID", if (syncedPrivateID != null) syncedPrivateID else firstPrivateID).findFirst()
        supportActionBar?.title = project.title
        if (setText) {
            editor.setText(project.code)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(@Suppress("UNUSED_PARAMETER") event: ProjectsSyncEvent) {
        if (event.message.startsWith("synced_id_")) {
            syncedPrivateID = event.message.replace("synced_id_", "")
            getProject(setText = false)

            saveCode()

        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        toolbar.menu.findItem(R.id.reload).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fontplus -> {
                setFontSize(codeSize + 1)
                FirebaseAnalytics.getInstance(this).logEvent("projects_edit_font_plus", Bundle())
                return true
            }
            R.id.fontminus -> {
                setFontSize(codeSize - 1)
                FirebaseAnalytics.getInstance(this).logEvent("projects_edit_font_minus", Bundle())
                return true
            }
            R.id.reload -> {
                loadResult()
                FirebaseAnalytics.getInstance(this).logEvent("projects_edit_reload", Bundle())
                return true
            }
            R.id.colorpicker -> {
                chooseColor(this, 0xffffffff.toInt())
                FirebaseAnalytics.getInstance(this).logEvent("projects_edit_colorpicker", Bundle())
                return true
            }
            android.R.id.home -> {
                saveCode()
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        saveCode()
        finish()
    }


    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }



}


