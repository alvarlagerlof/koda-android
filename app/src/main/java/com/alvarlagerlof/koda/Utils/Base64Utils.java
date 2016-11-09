package com.alvarlagerlof.koda.Utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by alvar on 2016-08-31.
 */
public class Base64Utils {

    public static String decode(String encoded) {

        if (encoded == null || encoded.equals("")) {
            return "";
        } else {
            byte[] data = android.util.Base64.decode(encoded, android.util.Base64.DEFAULT);

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
