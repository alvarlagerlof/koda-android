package com.alvarlagerlof.koda.Projects

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.view.Menu
import android.view.MenuItem
import com.alvarlagerlof.koda.R
import io.realm.Realm
import kotlinx.android.synthetic.main.projects_edit_activity.*
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit

class ProjectsEditActivity : AppCompatActivity() {

    lateinit var privateID: String
    lateinit var realm: Realm
    lateinit var realmObject: ProjectsRealmObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.projects_edit_activity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white)
        supportActionBar?.title = "Redigera info"

        // From intent
        if (intent.extras != null) {
            privateID = intent.extras.getString("privateID")
        }


        // Get and set values
        realm = Realm.getDefaultInstance()
        realmObject = realm.where(ProjectsRealmObject::class.java)
                .equalTo("privateID", privateID)
                .findFirst()


        // Set already saved values
        title_input.text = SpannableStringBuilder(realmObject.title)
        description_input.text = SpannableStringBuilder(realmObject.description)
        public_checkbox.isChecked = realmObject.isPublic


        // Click listners
        public_layout.setOnClickListener { public_checkbox.toggle() }



    }


    internal fun saveAndClose() {
        realm.beginTransaction()
        realmObject.title = if (title_input.text.toString() == "") getString(R.string.unnamed) else title_input.text.toString()
        realmObject.description = description_input.text.toString()
        realmObject.isPublic = public_checkbox.isChecked
        realmObject.synced = false
        realmObject.updatedRealm = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toString() + ""
        realm.commitTransaction()

        ProjectsSync(this)

        toast("Sparat")

        finish()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_projects_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveAndClose()
                return true
            }
            android.R.id.home -> {
                saveAndClose()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        saveAndClose()
    }
}
