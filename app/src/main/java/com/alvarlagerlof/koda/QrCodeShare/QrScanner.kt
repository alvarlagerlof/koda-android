package com.alvarlagerlof.koda.QrCodeShare

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.alvarlagerlof.koda.Extensions.base64Decode
import com.alvarlagerlof.koda.Extensions.hide
import com.alvarlagerlof.koda.Extensions.show
import com.alvarlagerlof.koda.Play.PlayActivity
import com.alvarlagerlof.koda.R
import kotlinx.android.synthetic.main.qr_scanner_activity.*
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by alvar-mac on 2/11/17.
 */

class QrScanner : AppCompatActivity() {


    private val FLASH_OFF = 0
    private val FLASH_ON = 1

    private var FLASH_STATE = FLASH_OFF


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_scanner_activity)


        // Set up toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white)


        // Scanner
        qr_scanner.setQRDecodingEnabled(true)
        qr_scanner.setAutofocusInterval(100L)
        qr_scanner.setBackCamera()
        qr_scanner.setOnQRCodeReadListener { text, _ ->
            try {
                val jsonObject = JSONObject(text)

                val intent = Intent(this@QrScanner, PlayActivity::class.java)
                intent.putExtra("url", jsonObject.getString("url").base64Decode())
                intent.putExtra("title", jsonObject.getString("title").base64Decode())
                intent.putExtra("author", jsonObject.getString("author").base64Decode())
                startActivity(intent)
                qr_scanner.setQRDecodingEnabled(false)

            } catch (ex: JSONException) {
                ex.printStackTrace()
            }
        }


    }





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scan, menu)

        if (FLASH_STATE == FLASH_ON) {
            menu.show(R.id.flash_on)
            menu.hide(R.id.flash_off)
        } else {
            menu.hide(R.id.flash_on)
            menu.show(R.id.flash_off)
        }

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.flash_off -> {
                qr_scanner.setTorchEnabled(true)
                FLASH_STATE = FLASH_ON
                invalidateOptionsMenu()
            }

            R.id.flash_on -> {
                qr_scanner.setTorchEnabled(false)
                FLASH_STATE = FLASH_OFF
                invalidateOptionsMenu()
            }

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }



    override fun onResume() {
        super.onResume()
        qr_scanner.startCamera()
        qr_scanner.setQRDecodingEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        qr_scanner.stopCamera()
    }

}
