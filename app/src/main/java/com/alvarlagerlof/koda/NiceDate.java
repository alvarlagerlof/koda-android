package com.alvarlagerlof.koda;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alvar on 2016-10-15.
 */

public class NiceDate {

    public static String convert(String timestamp) {

        Date date = new Date(Integer.parseInt(timestamp)*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM, yyyy"); // the format of your date

        return sdf.format(date);

    }

}
