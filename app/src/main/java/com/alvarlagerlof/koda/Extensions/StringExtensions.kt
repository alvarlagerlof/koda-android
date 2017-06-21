package com.alvarlagerlof.koda.Extensions

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by alvar on 2017-06-11.
 */

// Base64
fun String.base64Encode(): String {

    if (this == "") {
        return ""
    } else {
        var data: ByteArray? = null
        try {
            data = this.toByteArray(charset("UTF-8"))
        } catch (e1: UnsupportedEncodingException) {
            e1.printStackTrace()
        }

        return Base64.encodeToString(data, Base64.DEFAULT)
    }
}


fun String.base64Decode(): String {

    if (this == "") {
        return ""
    } else {
        val data = Base64.decode(this, android.util.Base64.DEFAULT)

        var decoded = "Error"

        try {
            decoded = String(data)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return decoded
    }
}


fun String.timeStampToDate(): String {

    if (this == "null") {
        return " (Error: felaktigt datum)"

    } else {
        val date = Date(Integer.parseInt(this) * 1000L) // *1000 is to convert seconds to milliseconds
        val sdf = SimpleDateFormat("d MMMM yyyy, HH:mm") // the format of your meta

        return sdf.format(date)

    }


}