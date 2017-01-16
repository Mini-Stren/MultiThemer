/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.ministren.multithemer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;

import java.util.ArrayList;
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
 * 3. Initialize {@link MultiThemer} in your {@link Application} class using method {@link #build(Application)}
 * <p>
 * 3.1. {@link #build(Application)} will return {@link Builder},
 * which you can use to add themes, set icon to use in recent apps list,
 * set SharedPreferences that will be used to save theme tag after theme change
 * and set by default one of {@link THEME} themes.
 * <p>
 * 3.2. If you will not use any of {@link Builder}{@code .addTheme()} methods,
 * it will automatically fill themes list with all predefined {@link THEME} themes.
 * It will use {@link THEME#INDIGO} as default theme,
 * or you can change it with {@link Builder#setDefault(THEME)}
 * <p>
 * 3.3. While adding your own themes using {@link Builder}{@code .addTheme()} methods,
 * don't forget to use for at least one theme method with parameter {@code isDefault}
 * <p>
 * 3.4. {@link Builder#setSharedPreferences(SharedPreferences)} is optional.
 * {@code DefaultSharedPreferences} will be used by default.
 * <p>
 * While defining your own themes in {@code styles.xml}, you can use following attributes:
 * {@code themeTextColorPrimary}, {@code themeTextColorPrimaryDark}, {@code themeTextColorAccent}
 * to set better text colors and use them in xml like that:
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
 * {@link Builder#useAppIcon(int)} and {@link Builder#useAppIcon(Bitmap)}
 * <p>
 * {@link #getThemesList()} and {@link #getActiveTheme()}
 * <p>
 * {@link THEME} and {@link ColorTheme}
 * <p>
 * {@link MultiThemeActivity}
 * <p>
 * <p>
 * Look at {@code https://github.com/Mini-Stren/MultiThemer} for more info, screenshots and demo app.
 * <p>
 * Created by Mini-Stren on 06.01.2017
 */

public class MultiThemer {

    public static String PREFERENCE_KEY = "com.ministren.multithemer.SAVED_TAG";
    private static String PREFERENCE_NO_VALUE = "com.ministren.multithemer.NO_SAVED_TAG_VALUE";
    private static boolean DEBUGGING = false;

    private static MultiThemer INSTANCE = null;
    private List<ColorTheme> mThemes;
    private SharedPreferences mPrefs;
    private String mActiveTag;
    private Bitmap mAppIcon;
    private boolean mInitialized = false;

    private MultiThemer() {
    }

    public static void setDebug(boolean flag) {
        DEBUGGING = flag;
    }

    public static MultiThemer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiThemer();
            if (DEBUGGING) {
                Log.d(Utils.LOG_TAG, "instance created");
            }
        }
        return INSTANCE;
    }

    /**
     * Creates a builder instance for this class to initialize library.
     *
     * @param application {@link Application} to attach to
     * @return {@link Builder} to initialize the library with
     */
    @NonNull
    public static Builder build(Application application) {
        return new Builder(application);
    }

    /**
     * Sets active theme to specified activity.
     * <p>
     * {@link MultiThemeActivity} calls this method in {@link MultiThemeActivity#onCreate(Bundle)}
     *
     * @param activity {@link Activity} to set theme
     */
    public void applyTheme(Activity activity) {
        checkInit();

        long startTime = System.currentTimeMillis();

        ColorTheme activeTheme = getActiveTheme();
        activity.setTheme(activeTheme.getStyleResID());

        if (Build.VERSION.SDK_INT >= 21 && mAppIcon != null) {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                    null, mAppIcon, activeTheme.getColorPrimaryDark()
            );
            activity.setTaskDescription(taskDescription);
        }

        if (DEBUGGING) {
            long time = System.currentTimeMillis() - startTime;
            Log.d(Utils.LOG_TAG, "theme applied in " + time + " milliseconds to activity " + activity.toString());
        }

    }

    /**
     * Checks for active theme and changes it to theme with specified tag if needed.
     *
     * @param tag {@link String} theme tag
     */
    public void changeTheme(String tag) {
        changeTheme(getTheme(tag));
    }

    /**
     * Checks for active theme and changes it to theme with specified style resource id if needed.
     *
     * @param styleResourceId theme style resource id
     */
    public void changeTheme(@StyleRes int styleResourceId) {
        changeTheme(getTheme(styleResourceId));
    }

    /**
     * Checks for active theme and changes it to specified theme if needed.
     *
     * @param theme {@link THEME} to set as active
     */
    public void changeTheme(THEME theme) {
        changeTheme(getTheme(theme.TAG));
    }

    /**
     * Checks for active theme and changes it to specified theme if needed.
     *
     * @param theme {@link ColorTheme} to set as active
     */
    public void changeTheme(ColorTheme theme) {
        checkInit();

        if (theme == null || !mThemes.contains(theme)) {
            Log.w(Utils.LOG_TAG, "changing theme error");
            return;
        }

        String savedTag = mPrefs.getString(PREFERENCE_KEY, PREFERENCE_NO_VALUE);
        if (theme.getTag().equals(savedTag)) {
            return;
        }

        if (DEBUGGING) {
            Log.d(Utils.LOG_TAG, "changing theme to " + theme.toString());
        }

        mActiveTag = theme.getTag();
        mPrefs.edit().putString(PREFERENCE_KEY, mActiveTag).apply();
    }

    public ColorTheme getActiveTheme() {
        return getTheme(mActiveTag);
    }

    public List<ColorTheme> getThemesList() {
        checkInit();
        return mThemes;
    }

    public SharedPreferences getSharedPreferences() {
        checkInit();
        return mPrefs;
    }

    /**
     * Looks for theme with specified tag in themes list.
     * Returns theme if founded, or null.
     *
     * @param tag {@link String} theme tag to find
     * @return {@link ColorTheme} or null
     */
    @Nullable
    public ColorTheme getTheme(String tag) {
        checkInit();

        for (ColorTheme theme : mThemes) {
            if (theme.getTag().equals(tag)) {
                return theme;
            }
        }

        if (DEBUGGING) {
            Log.d(Utils.LOG_TAG, "theme with tag '" + tag + "' not found");
        }
        return null;
    }

    /**
     * Looks for theme with specified style resource id in themes list.
     * Returns theme if founded, or null.
     *
     * @param styleResourceId style resource id to find
     * @return {@link ColorTheme} or null
     */
    @Nullable
    public ColorTheme getTheme(@StyleRes int styleResourceId) {
        checkInit();

        for (ColorTheme theme : mThemes) {
            if (theme.getStyleResID() == styleResourceId) {
                return theme;
            }
        }

        if (DEBUGGING) {
            Log.d(Utils.LOG_TAG, "theme with style resource id " + styleResourceId + " not found");
        }
        return null;
    }

    /**
     * Initializes MultiThemer with specified {@link Builder}.
     *
     * @param builder {@link Builder} instance
     */
    private void install(Builder builder) {
        if (builder.themes.isEmpty()) {
            throw new IllegalStateException("MultiThemer themes list is empty!");
        }

        mPrefs = builder.prefs;
        mThemes = new ArrayList<>(builder.themes);
        mAppIcon = builder.icon;

        String savedTag = mPrefs.getString(PREFERENCE_KEY, PREFERENCE_NO_VALUE);
        if (savedTag.equals(PREFERENCE_NO_VALUE) || !checkTheme(savedTag)) {
            Log.i(Utils.LOG_TAG, "theme with saved tag not found");
            if (!checkTheme(builder.defaultTag)) {
                throw new IllegalStateException("theme with tag '" + builder.defaultTag + "' not found");
            }
            Log.i(Utils.LOG_TAG, "saving tag '" + builder.defaultTag + "' as default");
            mActiveTag = builder.defaultTag;
            mPrefs.edit().putString(PREFERENCE_KEY, builder.defaultTag).apply();
        } else {
            Log.i(Utils.LOG_TAG, "restoring theme with saved tag '" + savedTag + "'");
            mActiveTag = savedTag;
        }

        mInitialized = true;

        if (DEBUGGING) {
            long time = System.currentTimeMillis() - builder.startTime;
            Log.d(Utils.LOG_TAG, "MultiThemer initialized in "
                    + time + " milliseconds to "
                    + builder.application.getPackageName());
        }
    }

    private void checkInit() {
        if (!mInitialized) {
            throw new IllegalStateException("MultiThemer is not initialized!");
        }
    }

    private boolean checkTheme(String tag) {
        for (ColorTheme theme : mThemes) {
            if (theme.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Enumeration of predefined by library themes
     */
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
        public final int STYLE_RES_ID;

        THEME(String tag, @StyleRes int styleResourceId) {
            TAG = tag;
            STYLE_RES_ID = styleResourceId;
        }

        public ColorTheme toColorTheme(Context context) {
            return new ColorTheme(context, TAG, STYLE_RES_ID);
        }

        @Override
        public String toString() {
            return "THEME { TAG='" + TAG + "'; STYLE_RES_ID=" + STYLE_RES_ID + " }";
        }
    }

    /**
     * Initialization builder
     */
    public static class Builder {
        private final List<ColorTheme> themes;
        private SharedPreferences prefs;
        private String defaultTag;
        private Bitmap icon;
        private Application application;
        private long startTime;

        Builder(@NonNull Application application) {
            startTime = System.currentTimeMillis();
            this.application = application;
            themes = new ArrayList<>();
        }

        /**
         * Adds {@link THEME} to themes list.
         *
         * @param theme {@link THEME} to add
         * @return this {@link Builder}
         */
        public Builder addTheme(THEME theme) {
            return addTheme(theme, false);
        }

        /**
         * Adds {@link THEME} to themes list and sets it as default by {@code isDefault} flag.
         *
         * @param theme     {@link THEME} to add
         * @param isDefault flag to use theme as default
         * @return this {@link Builder}
         */
        public Builder addTheme(THEME theme, boolean isDefault) {
            return addTheme(theme.toColorTheme(application), isDefault);
        }

        /**
         * Adds {@link ColorTheme} to themes list.
         *
         * @param theme {@link ColorTheme} to add
         * @return this {@link Builder}
         */
        public Builder addTheme(ColorTheme theme) {
            return addTheme(theme, false);
        }

        /**
         * Adds {@link ColorTheme} to themes list and sets it as default by {@code isDefault} flag.
         *
         * @param theme     {@link ColorTheme} to add
         * @param isDefault flag to use theme as default
         * @return this {@link Builder}
         */
        public Builder addTheme(ColorTheme theme, boolean isDefault) {
            checkForDuplicates(theme);

            if (isDefault) {
                defaultTag = theme.getTag();
            }
            themes.add(theme);

            if (DEBUGGING) {
                Log.d(Utils.LOG_TAG, "theme " + theme.toString() + " added to list");
            }
            return this;
        }

        /**
         * Adds {@link ColorTheme} to themes list with specified tag and style resource id.
         *
         * @param tag             {@link String} theme tag
         * @param styleResourceId theme style resource id
         * @return this {@link Builder}
         */
        public Builder addTheme(String tag, @StyleRes int styleResourceId) {
            return addTheme(tag, styleResourceId, false);
        }

        /**
         * Adds {@link ColorTheme} to themes list with specified tag and style resource id
         * and sets it as default by {@code isDefault} flag.
         *
         * @param tag             {@link String} theme tag
         * @param styleResourceId theme style resource id
         * @param isDefault       flag to use theme as default
         * @return this {@link Builder}
         */
        public Builder addTheme(String tag, @StyleRes int styleResourceId, boolean isDefault) {
            ColorTheme theme = new ColorTheme(application, tag, styleResourceId);
            return addTheme(theme, isDefault);
        }

        /**
         * Sets specified {@link THEME} to use as default theme.
         *
         * @param theme {@link THEME}
         * @return this {@link Builder}
         */
        public Builder setDefault(THEME theme) {
            defaultTag = theme.TAG;
            return this;
        }

        /**
         * Sets specified drawable resource id to use as app icon in recent apps list.
         * Doesn't affect Android SDK below 21.
         *
         * @param iconResourceId drawable resource id to use as app icon
         * @return this {@link Builder}
         */
        public Builder useAppIcon(@DrawableRes int iconResourceId) {
            icon = BitmapFactory.decodeResource(application.getResources(), iconResourceId);
            return this;
        }

        /**
         * Sets specified {@link Bitmap} to use as app icon in recent apps list.
         * Doesn't affect Android SDK below 21.
         *
         * @param iconBitmap {@link Bitmap} to use as app icon
         * @return this {@link Builder}
         */
        public Builder useAppIcon(Bitmap iconBitmap) {
            icon = iconBitmap;
            return this;
        }

        /**
         * Sets specified {@link SharedPreferences} that will be used to save theme tag after theme change.
         *
         * @param prefs {@link SharedPreferences} instance
         * @return this {@link Builder}
         */
        public Builder setSharedPreferences(SharedPreferences prefs) {
            this.prefs = prefs;
            return this;
        }

        /**
         * Method to initialize {@link MultiThemer} instance.
         * Will set {@link THEME#INDIGO} as default theme if saved tag won't found.
         */
        public void initialize() {
            if (prefs == null) {
                prefs = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
            }
            if (themes.isEmpty()) {
                Log.i(Utils.LOG_TAG, "no themes was added, initializing with default themes list");
                for (THEME theme : THEME.values()) {
                    addTheme(theme);
                }
                if (defaultTag == null) {
                    defaultTag = THEME.INDIGO.TAG;
                }
            }
            if (defaultTag == null) {
                throw new IllegalStateException("default theme wasn't set");
            }
            MultiThemer.getInstance().install(this);
        }

        private void checkForDuplicates(ColorTheme theme) {
            String tag = theme.getTag();
            for (ColorTheme colorTheme : themes) {
                if (colorTheme.getTag().equals(tag)) {
                    throw new IllegalStateException(
                            "duplicate themes with tag '" + tag + "' found in themes list"
                    );
                }
            }

            int styleResId = theme.getStyleResID();
            for (ColorTheme colorTheme : themes) {
                if (colorTheme.getStyleResID() == styleResId) {
                    throw new IllegalStateException(
                            "duplicate themes with style resource id " + styleResId + " found in themes list"
                    );
                }
            }
        }

    }

}
