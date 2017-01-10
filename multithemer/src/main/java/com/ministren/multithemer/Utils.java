package com.ministren.multithemer;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;

/**
 * Created by Mini-Stren on 06.01.2017
 */

final class Utils {

    static String LOG_TAG = "MultiThemer";

    private Utils() {
        throw new AssertionError();
    }

    @ColorInt
    static int getColorStyleAttr(Context context, @StyleRes int styleID, @AttrRes int attr) {
        TypedArray typedArray = context.obtainStyledAttributes(styleID, new int[]{attr});
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;
    }
}
