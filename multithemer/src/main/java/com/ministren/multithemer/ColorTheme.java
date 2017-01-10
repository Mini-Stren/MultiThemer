package com.ministren.multithemer;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;

/**
 * Created by Mini-Stren on 06.01.2017
 */

public class ColorTheme {

    private Context CONTEXT;
    private String TAG;
    @StyleRes
    private int STYLE_RES_ID;

    public ColorTheme(Context context, String tag, @StyleRes int styleResID) {
        CONTEXT = context;
        TAG = tag;
        STYLE_RES_ID = styleResID;
    }

    @Override
    public String toString() {
        return "ColorTheme { TAG='" + TAG + "'; STYLE_RES_ID=" + STYLE_RES_ID + " }";
    }

    public String getTag() {
        return TAG;
    }

    @StyleRes
    public int getStyleResID() {
        return STYLE_RES_ID;
    }

    @ColorInt
    public int getColorPrimary() {
        return Utils.getColorStyleAttr(CONTEXT, STYLE_RES_ID, R.attr.colorPrimary);
    }

    @ColorInt
    public int getColorPrimaryDark() {
        return Utils.getColorStyleAttr(CONTEXT, STYLE_RES_ID, R.attr.colorPrimaryDark);
    }

    @ColorInt
    public int getColorAccent() {
        return Utils.getColorStyleAttr(CONTEXT, STYLE_RES_ID, R.attr.colorAccent);
    }

    @ColorInt
    public int getTextColorPrimary() {
        return Utils.getColorStyleAttr(CONTEXT, STYLE_RES_ID, R.attr.themeTextColorPrimary);
    }

    @ColorInt
    public int getTextColorPrimaryDark() {
        return Utils.getColorStyleAttr(CONTEXT, STYLE_RES_ID, R.attr.themeTextColorPrimaryDark);
    }

    @ColorInt
    public int getTextColorAccent() {
        return Utils.getColorStyleAttr(CONTEXT, STYLE_RES_ID, R.attr.themeTextColorAccent);
    }

    Context getContext() {
        return CONTEXT;
    }
}
