package com.alvarlagerlof.koda.Settings.About

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.alvarlagerlof.koda.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.about_activity.*

class AboutActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_activity)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white)
        supportActionBar?.title = "Om Koda.nu"


        // Set data
        val app_description = "Den här applikationen skapades 2016 som ett projekt för " +
                "Internetfonden, internetfonden.se\n\n" +

                "Internetfonden finansieras av " +
                "Internetstiftelsen i Sverige, IIS, som jobbar med att driva på en " +
                "positiv utveckling av Internet i Sverige.\n\n" +

                "Målet med koda.nu är att göra det lättare och roligare att lära sig programmering.\n\n" +

                "Apputvecklare: Alvar Lagerlöf\n" +
                "Instagram: instagram.com/alvarlagerlof\n" +
                "Github: github.com/alvarlagerlof\n\n" +

                "Ansvarig för koda.nu: Mikael Tylmad\n" +
                "Epost: mikael@roboro.se"

        description.text = app_description


        var pInfo: PackageInfo? = null
        try {
            pInfo = this@AboutActivity.packageManager.getPackageInfo(this@AboutActivity.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        var version = pInfo!!.versionName
        val versionCode = pInfo.versionCode
        version = "Version " + version + " (" + versionCode.toString() + ")\nCC BY-SA 3.0"

        this.version.text = version

        Glide.with(this@AboutActivity)
                .load(R.mipmap.ic_launcher)
                .into(icon)

    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}
