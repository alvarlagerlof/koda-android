package com.alvarlagerlof.koda.Projects

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import com.alvarlagerlof.koda.R
import com.google.firebase.analytics.FirebaseAnalytics
import io.realm.Realm

/**
 * Created by alvar on 2016-11-08.
 */

internal class ProjectsAdd(context: Context) {


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.projects_add, null)

        val input: EditText = view.findViewById(R.id.input)

        val dialog = AlertDialog.Builder(context)
                .setTitle("Vad ska den heta?")
                .setView(view)
                .setPositiveButton("OK") { _, _ ->
                    val title = if (input.text.toString() == "") "Namnlös" else input.text.toString()

                    // Temporary privateID
                    val tmpPrivateID = "ny_ny_" + System.currentTimeMillis().toString()

                    // Add to realm
                    Realm.getDefaultInstance().executeTransaction { realm ->
                        val realmObject = realm.createObject(ProjectsRealmObject::class.java)
                        realmObject.privateID = tmpPrivateID
                        realmObject.publicID = tmpPrivateID
                        realmObject.title = title
                        realmObject.updatedRealm = (System.currentTimeMillis() / 1000).toString()
                        realmObject.description = ""
                        realmObject.isPublic = false
                        realmObject.synced = false
                        realmObject.code = "<script src=\"http://koda.nu/simple.js\">\n" +
                                "\n" +
                                "  circle(100, 100, 20, \"red\");\n" +
                                "\n" +
                                "</script>"
                    }


                    // Sync
                    ProjectsSync(context, tmpPrivateID)

                    FirebaseAnalytics.getInstance(context).logEvent("projects_add", Bundle())


                }
                .setNegativeButton("Avbryt") {
                    dialog, _ -> dialog.cancel()
                }
                .show()

        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)



    }





}
