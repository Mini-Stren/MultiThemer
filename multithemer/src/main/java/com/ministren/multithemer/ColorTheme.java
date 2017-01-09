package com.ministren.multithemer;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;

/**
 * Created by Mini-Stren on 06.01.2017
 */

public class ColorTheme {

    private String TAG;
    @StyleRes
    private int STYLE_ID;
    @ColorInt
    private int COLOR_PRIMARY;
    @ColorInt
    private int COLOR_PRIMARY_DARK;
    @ColorInt
    private int COLOR_ACCENT;
    @ColorInt
    private int TEXT_COLOR_PRIMARY;
    @ColorInt
    private int TEXT_COLOR_PRIMARY_DARK;
    @ColorInt
    private int TEXT_COLOR_ACCENT;

    public ColorTheme(Context context, String tag, @StyleRes int styleID) {
        TAG = tag;
        STYLE_ID = styleID;
        COLOR_PRIMARY = Utils.getThemeAttrColor(context, STYLE_ID, R.attr.colorPrimary);
        COLOR_PRIMARY_DARK = Utils.getThemeAttrColor(context, STYLE_ID, R.attr.colorPrimaryDark);
        COLOR_ACCENT = Utils.getThemeAttrColor(context, STYLE_ID, R.attr.colorAccent);
        TEXT_COLOR_PRIMARY = Utils.getThemeAttrColor(context, STYLE_ID, R.attr.themeTextColorPrimary);
        TEXT_COLOR_PRIMARY_DARK = Utils.getThemeAttrColor(context, STYLE_ID, R.attr.themeTextColorPrimaryDark);
        TEXT_COLOR_ACCENT = Utils.getThemeAttrColor(context, STYLE_ID, R.attr.themeTextColorAccent);
    }

    @Override
    public String toString() {
        return "{ Tag: '" + TAG + "', StyleID: '" + STYLE_ID + "' }";
    }

    public String getTag() {
        return TAG;
    }

    @StyleRes
    public int getStyleID() {
        return STYLE_ID;
    }

    @ColorInt
    public int getColorPrimary() {
        return COLOR_PRIMARY;
    }

    @ColorInt
    public int getColorPrimaryDark() {
        return COLOR_PRIMARY_DARK;
    }

    @ColorInt
    public int getColorAccent() {
        return COLOR_ACCENT;
    }

    @ColorInt
    public int getTextColorPrimary() {
        return TEXT_COLOR_PRIMARY;
    }

    @ColorInt
    public int getTextColorPrimaryDark() {
        return TEXT_COLOR_PRIMARY_DARK;
    }

    @ColorInt
    public int getTextColorAccent() {
        return TEXT_COLOR_ACCENT;
    }
}
