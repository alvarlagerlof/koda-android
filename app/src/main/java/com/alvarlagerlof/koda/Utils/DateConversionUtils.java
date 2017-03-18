package com.alvarlagerlof.koda.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alvar on 2016-10-15.
 */

public class DateConversionUtils {

    public static String convert(String timestamp) {


        if (timestamp.equals("null")) {

            return " (Error: felaktigt datum)";

        } else {

            Date date = new Date(Integer.parseInt(timestamp)*1000L); // *1000 is to convert seconds to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, d MMMM, yyyy"); // the format of your meta

            return sdf.format(date);

        }



    }

}
