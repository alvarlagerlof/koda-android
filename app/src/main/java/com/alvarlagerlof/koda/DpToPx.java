package com.alvarlagerlof.koda;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by alvar on 2016-11-08.
 */

public class DpToPx {

    public int convert(Context context, int dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

}
