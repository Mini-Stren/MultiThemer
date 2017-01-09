package com.ministren.multithemer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link MultiThemer} provides easy way to use as many app themes as you would like.
 * {@link MultiThemer} will save last used app's theme and restore it in app launch.
 * <p>
 * All you need to do is:
 * <p>
 * 1. Extend activity from {@link MultiThemeActivity} to let it
 * automatically apply app's active theme and restart after theme change.
 * <p>
 * 2. Define your themes in {@code styles.xml}, or use up to 16 predefined themes in {@link THEME}
 * <p>
 * 3. Initialize {@link MultiThemer} in {@link Application} class using one of the following methods:
 * {@link #install(Application)},
 * or {@link #install(Application, THEME)},
 * or {@link #install(Application, List, String)}
 * <p>
 * While defining your own themes, you can use following attributes:
 * {@code themeTextColorPrimary}, {@code themeTextColorPrimaryDark}, {@code themeTextColorAccent}
 * to set better text colors and use them in xml:
 * {@code ?attr/themeTextColorAccent}
 * or get them in code by calling
 * {@link ColorTheme#getTextColorPrimary()},
 * {@link ColorTheme#getTextColorPrimaryDark()},
 * {@link ColorTheme#getTextColorAccent()}
 * <p>
 * You can use {@link MultiThemerListFragment} to present all app's themes
 * and give users easy way to switch between themes.
 * <p>
 * See also:
 * <p>
 * {@link #useAppIcon(Context, int)} and {@link #useAppIcon(Bitmap)}
 * <p>
 * {@link #getThemesList()} and {@link #getActiveTheme()} and {@link #clear()}
 * <p>
 * {@link THEME} and {@link ColorTheme}
 * <p>
 * {@link MultiThemeActivity}
 * <p>
 * <p>
 * Look at {@code https://github.com/Mini-Stren} for more info, screenshots and demo app.
 * <p>
 * Created by Mini-Stren on 06.01.2017
 */

public final class MultiThemer {

    private static List<ColorTheme> mThemesList;
    private static String mActiveThemeTag;
    private static Bitmap mAppIcon;

    private MultiThemer() {
        throw new AssertionError();
    }

    /**
     * Initializes MultiThemer with default themes list.
     * Uses {@code THEME.INDIGO} as start theme if saved tag won't found.
     *
     * @param application {@link Application} instance
     */
    public static void install(Application application) {
        install(application, THEME.INDIGO);
    }

    /**
     * Initializes MultiThemer with default themes list.
     * Uses specified {@link THEME} as start theme if saved tag won't found.
     *
     * @param application {@link Application} instance
     * @param startTheme  {@link THEME} to use as start theme
     */
    public static void install(Application application, THEME startTheme) {
        Log.d(Utils.LOG_TAG, "attaching to " + application.getPackageName());
        setDefaultThemesList(application);
        String savedThemeTag = Utils.getSavedThemeTag(application);
        if (savedThemeTag.equals(Utils.NO_KEY_VALUE) || getThemeByTag(savedThemeTag) == null) {
            setActiveTheme(application, startTheme);
        } else {
            Log.d(Utils.LOG_TAG, "setting active theme with saved tag '" + savedThemeTag + "'");
            setActiveTheme(application, savedThemeTag);
        }
    }

    /**
     * Initializes MultiThemer with specified themes list.
     * Uses specified tag to set start theme if saved tag won't found.
     *
     * @param application   {@link Application} instance
     * @param themesList    {@link List<ColorTheme>} List of app themes
     * @param startThemeTag Tag to set start theme
     */
    public static void install(Application application, List<ColorTheme> themesList, String startThemeTag) {
        Log.d(Utils.LOG_TAG, "attaching to " + application.getPackageName());
        clear();
        for (ColorTheme theme : themesList) {
            addTheme(theme);
        }
        String savedThemeTag = Utils.getSavedThemeTag(application);
        if (savedThemeTag.equals(Utils.NO_KEY_VALUE) || getThemeByTag(savedThemeTag) == null) {
            setActiveTheme(application, startThemeTag);
        } else {
            Log.d(Utils.LOG_TAG, "setting active theme with saved tag '" + savedThemeTag + "'");
            setActiveTheme(application, savedThemeTag);
        }
    }

    /**
     * Sets {@link Bitmap} to use as app icon in recent apps list.
     * Doesn't affect Android SDK below 21.
     *
     * @param context      {@link Context} instance
     * @param appIconResID Drawable resource id to use as app icon
     */
    public static void useAppIcon(Context context, @DrawableRes int appIconResID) {
        try {
            mAppIcon = BitmapFactory.decodeResource(context.getResources(), appIconResID);
        } catch (Exception e) {
            mAppIcon = null;
            Log.w(Utils.LOG_TAG, "app icon set fail", e);
        }
    }

    /**
     * Sets {@link Bitmap} to use as app icon in recent apps list.
     * Doesn't affect Android SDK below 21.
     *
     * @param iconBitmap {@link Bitmap} to use as app icon
     */
    public static void useAppIcon(Bitmap iconBitmap) {
        mAppIcon = iconBitmap;
    }

    /**
     * Sets active theme to specified activity. If active theme is null,
     * checks themes list and tries to set first theme from list.
     * <p>
     * {@link MultiThemeActivity} calls this method in {@link MultiThemeActivity#onCreate(Bundle)}
     *
     * @param activity {@link Activity} instance
     */
    public static void applyTheme(Activity activity) {
        long startTime = System.currentTimeMillis();

        ColorTheme activeTheme = getActiveTheme();
        if (activeTheme == null) {
            Log.w(Utils.LOG_TAG, "active theme is null, no theme will be applied to activity");
            return;
        }

        activity.setTheme(activeTheme.getStyleID());

        if (Build.VERSION.SDK_INT >= 21 && mAppIcon != null) {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                    null, mAppIcon, activeTheme.getColorPrimaryDark()
            );
            activity.setTaskDescription(taskDescription);
        }

        long time = System.currentTimeMillis() - startTime;
        Log.d(Utils.LOG_TAG, "theme applied in " + time + " milliseconds to activity " + activity.toString());
    }

    @Nullable
    public static List<ColorTheme> getThemesList() {
        return mThemesList;
    }

    @Nullable
    public static ColorTheme getActiveTheme() {
        return getThemeByTag(mActiveThemeTag);
    }

    /**
     * Looks for theme with specified tag in themes list.
     * Returns theme if founded, or null.
     *
     * @param themeTag Theme tag to find
     * @return {@link ColorTheme} or null
     */
    @Nullable
    public static ColorTheme getThemeByTag(String themeTag) {
        for (ColorTheme theme : mThemesList) {
            if (theme.getTag().equals(themeTag)) {
                return theme;
            }
        }
        Log.d(Utils.LOG_TAG, "theme with tag '" + themeTag + "' not found");
        return null;
    }

    /**
     * Looks for theme with specified style resource id in themes list.
     * Returns theme if founded, or null.
     *
     * @param styleID Style resource id to find
     * @return {@link ColorTheme} or null
     */
    @Nullable
    public static ColorTheme getThemeByStyleID(@StyleRes int styleID) {
        for (ColorTheme theme : mThemesList) {
            if (theme.getStyleID() == styleID) {
                return theme;
            }
        }
        Log.d(Utils.LOG_TAG, "theme with style id '" + styleID + "' not found");
        return null;
    }

    /**
     * Adds new {@link THEME} to themes list if it doesn't contain it.
     *
     * @param context {@link Context} instance
     * @param theme   {@link THEME} to add
     */
    public static void addTheme(Context context, THEME theme) {
        addTheme(theme.toColorTheme(context));
    }

    /**
     * Adds new {@link ColorTheme} to themes list with specified tag
     * and style resource id if it doesn't contain it.
     *
     * @param context {@link Context} instance
     * @param tag     Theme tag
     * @param styleID Theme style resource id
     */
    public static void addTheme(Context context, String tag, @StyleRes int styleID) {
        addTheme(new ColorTheme(context, tag, styleID));
    }

    /**
     * Adds new {@link ColorTheme} to themes list if it doesn't contain it.
     *
     * @param theme {@link ColorTheme} to add
     */
    public static void addTheme(ColorTheme theme) {
        if (notInList(theme)) {
            mThemesList.add(theme);
            Log.d(Utils.LOG_TAG, "theme " + theme.toString() + " added to list");
        } else {
            Log.d(Utils.LOG_TAG, "theme " + theme.toString() + " was not added to list");
        }
    }

    /**
     * Sets specified {@link THEME} as active theme if it is in themes list.
     *
     * @param context {@link Context} instance
     * @param theme   {@link THEME} to set
     */
    public static void setActiveTheme(Context context, THEME theme) {
        setActiveTheme(context, theme.TAG);
    }

    /**
     * Sets specified {@link ColorTheme} as active theme if it is in themes list.
     *
     * @param context {@link Context} instance
     * @param theme   {@link ColorTheme} to set
     */
    public static void setActiveTheme(Context context, ColorTheme theme) {
        setActiveTheme(context, theme.getTag());
    }

    /**
     * Sets theme with specified tag as active theme if it is in themes list.
     *
     * @param context  {@link Context} instance
     * @param themeTag Theme tag
     */
    public static void setActiveTheme(Context context, String themeTag) {
        if (mActiveThemeTag != null && mActiveThemeTag.equals(themeTag)) {
            return;
        }
        if (getThemeByTag(themeTag) != null) {
            mActiveThemeTag = themeTag;
            Utils.saveThemeTag(context, themeTag);
        } else {
            Log.w(Utils.LOG_TAG, "error set active theme, theme with tag '" + themeTag + "' not found");
        }
    }

    /**
     * Clears themes list and sets active theme tag to null.
     */
    public static void clear() {
        mThemesList = new LinkedList<>();
        mActiveThemeTag = null;
        Log.d(Utils.LOG_TAG, "themes list has been cleared, active theme tag set to null");
    }

    private static void setDefaultThemesList(Context context) {
        Log.d(Utils.LOG_TAG, "setting default themes list");
        clear();
        addTheme(context, THEME.RED);
        addTheme(context, THEME.PINK);
        addTheme(context, THEME.PURPLE);
        addTheme(context, THEME.DEEP_PURPLE);
        addTheme(context, THEME.INDIGO);
        addTheme(context, THEME.BLUE);
        addTheme(context, THEME.LIGHT_BLUE);
        addTheme(context, THEME.CYAN);
        addTheme(context, THEME.TEAL);
        addTheme(context, THEME.GREEN);
        addTheme(context, THEME.LIGHT_GREEN);
        addTheme(context, THEME.LIME);
        addTheme(context, THEME.YELLOW);
        addTheme(context, THEME.AMBER);
        addTheme(context, THEME.ORANGE);
        addTheme(context, THEME.DEEP_ORANGE);
    }

    private static boolean notInList(ColorTheme theme) {
        if (getThemeByTag(theme.getTag()) != null) {
            Log.i(Utils.LOG_TAG, "theme with tag '" + theme.getTag() + "' already in list");
            return false;
        }
        if (getThemeByStyleID(theme.getStyleID()) != null) {
            Log.i(Utils.LOG_TAG, "theme with styleID '" + theme.getStyleID() + "' already in list");
            return false;
        }
        return true;
    }

    public enum THEME {
        RED("Red", R.style.MultiThemer_Red),
        PINK("Pink", R.style.MultiThemer_Pink),
        PURPLE("Purple", R.style.MultiThemer_Purple),
        DEEP_PURPLE("Deep Purple", R.style.MultiThemer_DeepPurple),
        INDIGO("Indigo", R.style.MultiThemer_Indigo),
        BLUE("Blue", R.style.MultiThemer_Blue),
        LIGHT_BLUE("Light Blue", R.style.MultiThemer_LightBlue),
        CYAN("Cyan", R.style.MultiThemer_Cyan),
        TEAL("Teal", R.style.MultiThemer_Teal),
        GREEN("Green", R.style.MultiThemer_Green),
        LIGHT_GREEN("Light Green", R.style.MultiThemer_LightGreen),
        LIME("Lime", R.style.MultiThemer_Lime),
        YELLOW("Yellow", R.style.MultiThemer_Yellow),
        AMBER("Amber", R.style.MultiThemer_Amber),
        ORANGE("Orange", R.style.MultiThemer_Orange),
        DEEP_ORANGE("Deep Orange", R.style.MultiThemer_DeepOrange);

        public final String TAG;
        @StyleRes
        public final int STYLE_ID;

        THEME(String tag, @StyleRes int styleID) {
            TAG = tag;
            STYLE_ID = styleID;
        }

        public ColorTheme toColorTheme(Context context) {
            return new ColorTheme(context, TAG, STYLE_ID);
        }

        @Override
        public String toString() {
            return "{ Tag: '" + TAG + "', StyleID: '" + STYLE_ID + "' }";
        }
    }
}
