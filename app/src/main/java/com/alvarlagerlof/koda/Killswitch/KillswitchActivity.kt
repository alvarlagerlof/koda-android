package com.alvarlagerlof.koda.Killswitch

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alvarlagerlof.koda.R
import com.alvarlagerlof.koda.Vars
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.killswitch_activity.*

/**
 * Created by alvar on 2017-05-28.
 */

class KillswitchActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.killswitch_activity)

        FirebaseAnalytics.getInstance(this).logEvent("killswitch_open", Bundle())


        if (intent.extras.getBoolean("didNotFetch")) {
            title_text.text = "Tjänsten nere."
            description_text.text = "Appen kunde inte prata med servern."
            action_text.text = "Pröva att gå in på appen igen om en stund"
        } else {
            title_text.text = Vars.KILLSWITCH_TITLE
            description_text.text = Vars.KILLSWITCH_DESCRIPTION
            action_text.text = Vars.KILLSWITCH_ACTION
        }


    }


    override fun onBackPressed() {
        // Do Here what ever you want do on back press;
    }


}
