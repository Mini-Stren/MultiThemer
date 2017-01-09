package com.ministren.multithemer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;
import android.util.Log;

/**
 * Created by Mini-Stren on 06.01.2017
 */

final class Utils {

    static final String LOG_TAG = "MultiThemer";
    static final String PREFERENCE_KEY = "MultiThemer_Saved_Theme_Name";
    static final String NO_KEY_VALUE = "NO_SAVED_THEME_TAG";

    private Utils() {
        throw new AssertionError();
    }

    static SharedPreferences getAppPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    static String getSavedThemeTag(Context context) {
        return getAppPrefs(context).getString(PREFERENCE_KEY, NO_KEY_VALUE);
    }

    static void saveThemeTag(Context context, String tag) {
        if (!getSavedThemeTag(context).equals(tag)) {
            Log.i(Utils.LOG_TAG, "saving theme tag: '" + tag + "'");
            getAppPrefs(context).edit()
                    .putString(Utils.PREFERENCE_KEY, tag)
                    .apply();
        }
    }

    @ColorInt
    static int getThemeAttrColor(Context context, @StyleRes int styleID, @AttrRes int attr) {
        TypedArray typedArray = context.obtainStyledAttributes(styleID, new int[]{attr});
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;
    }
}
