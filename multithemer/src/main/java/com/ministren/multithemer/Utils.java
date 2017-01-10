package com.ministren.multithemer;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;

/**
 * Created by Mini-Stren on 06.01.2017
 */

public final class Utils {

    static String LOG_TAG = "MultiThemer";

    private Utils() {
        throw new AssertionError();
    }

    @ColorInt
    public static int getColorStyleAttr(Context context, @StyleRes int styleResId, @AttrRes int attr) {
        TypedArray typedArray = context.obtainStyledAttributes(styleResId, new int[]{attr});
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;
    }

    @ColorInt
    public static int getColorThemeAttr(ColorTheme theme, @AttrRes int attr) {
        return getColorStyleAttr(theme.getContext(), theme.getStyleResID(), attr);
    }
}
