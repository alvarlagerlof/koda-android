package com.alvarlagerlof.koda.QrCodeShare

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.alvarlagerlof.koda.Extensions.base64Encode
import com.alvarlagerlof.koda.Extensions.hideKeyboard
import com.alvarlagerlof.koda.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import kotlinx.android.synthetic.main.qr_view_activity.*
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by alvar on 2016-09-01.
 */
class QrViewer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_view_activity)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white)
        supportActionBar?.title = "Dela ${intent.extras.getString("title")}"



        val data = JSONObject()
        try {
            data.put("url", intent.extras.getString("url").base64Encode())
            data.put("title", intent.extras.getString("title").base64Encode())
            data.put("author", intent.extras.getString("author").base64Encode())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val generate = generate()
        generate.execute(data.toString())

    }

    internal inner class generate : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg strings: String): Bitmap {

            var bitmap: Bitmap? = null

            try {
                bitmap = encodeAsBitmap(strings[0])
            } catch (e: WriterException) {
                e.printStackTrace()
            }

            return bitmap as Bitmap
        }

        override fun onPostExecute(bitmap: Bitmap?) {

            if (bitmap != null) {

                qr_image.setImageBitmap(bitmap)

                progress_bar.visibility = View.GONE
                qr_image.visibility = View.VISIBLE
                how_to_scan.visibility = View.VISIBLE


            }
        }
    }


    @Throws(WriterException::class)
    internal fun encodeAsBitmap(str: String): Bitmap? {
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 250, 250, null)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }

        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0..h - 1) {
            val offset = y * w
            for (x in 0..w - 1) {
                pixels[offset + x] = if (result.get(x, y)) ContextCompat.getColor(this, R.color.black) else ContextCompat.getColor(this, R.color.white)
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, 250, 0, 0, w, h)
        return bitmap
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            android.R.id.home -> {
                appbar_layout.hideKeyboard()
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


}
