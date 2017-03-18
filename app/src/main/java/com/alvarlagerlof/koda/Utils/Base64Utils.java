package com.alvarlagerlof.koda.Utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by alvar on 2016-08-31.
 */
public class Base64Utils {


    public static String encode(String decoded) {

        if (decoded == null || decoded.equals("")) {
            return "";
        } else {
            byte[] data = null;
            try {
                data = decoded.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            return Base64.encodeToString(data, Base64.DEFAULT);
        }
    }


    public static String decode(String encoded) {

        if (encoded == null || encoded.equals("")) {
            return "";
        } else {
            byte[] data = Base64.decode(encoded, android.util.Base64.DEFAULT);

            String decoded = "Error";

            try {
                decoded = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return decoded;
        }
    }
}
