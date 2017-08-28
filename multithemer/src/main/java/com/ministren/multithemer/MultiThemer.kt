/**
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

package com.ministren.multithemer

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.preference.PreferenceManager
import android.support.annotation.DrawableRes
import android.support.annotation.StyleRes
import android.util.Log

/**
 * {@link MultiThemer} provides easy way to use as many app themes as you would like.
 * {@link MultiThemer} will save last used app's theme and restore it in app launch.
 * <p>
 * All you need to do is:
 * <p>
 * 1. Extend activity from {@link MultiThemeActivity} to let it
 * automatically apply app's active theme and restart after theme change.
 * <p>
 * 2. Define your themes in {@code styles.xml}, or use up to 20 predefined themes in {@link THEME}.
 * <p>
 * 3. Initialize {@link MultiThemer} in your {@link Application} class using method {@link #build(Application)}.
 * <p>
 * 3.1. {@link #build(Application)} will return {@link Builder},
 * which you can use to add themes, set icon to use in recent apps list,
 * set SharedPreferences that will be used to save theme tag after theme change
 * and set by default one of {@link THEME} themes.
 * <p>
 * 3.2. If you will not use any of {@link Builder}{@code .addTheme()} methods,
 * it will automatically fill themes list with all predefined {@link THEME} themes.
 * It will use {@link THEME#INDIGO} as default theme,
 * or you can change it with {@link Builder#setDefault(THEME)}.
 * <p>
 * 3.3. While adding your own themes using one of {@link Builder}{@code .addTheme()} methods,
 * don't forget to use for at least one {@code .addTheme()} method with parameter {@code isDefault},
 * otherwise the first theme in the list will be used by default one.
 * <p>
 * 3.4. {@link Builder#setSharedPreferences(SharedPreferences)} is optional.
 * {@code DefaultSharedPreferences} will be used by default.
 * <p>
 * You can use {@link MultiThemerListFragment} to present all app's themes
 * and give users easy way to switch between themes,
 * or use {@link #getThemesList()} and {@link #getActiveTheme()} to write your own theme chooser.
 * <p>
 * See also:
 * <p>
 * {@link Builder#useAppIcon(int)} and {@link Builder#useAppIcon(Bitmap)}
 * <p>
 * {@link THEME} and {@link ColorTheme}
 * <p>
 * {@link MultiThemeActivity}
 * <p>
 * <p>
 * Look at {@code https://github.com/Mini-Stren/MultiThemer} for more info, screenshots and demo app.
 * <p>
 *
 * Created by Mini-Stren on 28.08.2017.
 */

class MultiThemer private constructor() {

    private lateinit var mThemes: List<ColorTheme>
    private lateinit var mPrefs: SharedPreferences
    private lateinit var mActiveThemeTag: String
    private var mAppIcon: Bitmap? = null
    private var mInitialized = false

    private object Holder {
        val INSTANCE = MultiThemer()
    }

    companion object {
        internal val PREFERENCE_KEY = "com.ministren.multithemer.SAVED_TAG"
        internal val PREFERENCE_NO_VALUE = "com.ministren.multithemer.NO_SAVED_TAG_VALUE"
        internal val LOG_TAG = "MultiThemer"

        val instance: MultiThemer by lazy {
            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "instance created")
            }
            Holder.INSTANCE
        }

        /**
         * Creates a builder instance for this class to initialize library.
         *
         * @param application {@link Application} to attach to
         * @return {@link Builder} to initialize the library with
         */
        fun build(application: Application) = Builder(application)
    }

    /**
     * Sets active theme to specified activity.
     * <p>
     * {@link MultiThemeActivity} calls this method in {@link MultiThemeActivity#onCreate(Bundle)}
     *
     * @param activity {@link Activity} to set theme
     */
    fun applyThemeTo(activity: Activity) {
        checkInit()

        val startTime = System.currentTimeMillis()

        val activeTheme = getActiveTheme()
        activity.setTheme(activeTheme!!.styleResID)

        if (Build.VERSION.SDK_INT >= 21 && mAppIcon != null) {
            val taskDescription = ActivityManager.TaskDescription(
                    null, mAppIcon, activeTheme.getAttrColor(R.attr.colorPrimary))
            activity.setTaskDescription(taskDescription)
        }

        if (BuildConfig.DEBUG) {
            val time = System.currentTimeMillis() - startTime
            Log.d(LOG_TAG, "theme applied in $time milliseconds to activity $activity")
        }
    }

    fun getThemesList(): List<ColorTheme> {
        checkInit()
        return mThemes
    }

    fun getSharedPreferences(): SharedPreferences {
        checkInit()
        return mPrefs
    }

    /**
     * Looks for theme with specified tag in themes list.
     * Returns theme if founded, or null.
     *
     * @param tag {@link String} theme tag to find
     * @return {@link ColorTheme} or null
     */
    fun getTheme(tag: String): ColorTheme? {
        checkInit()
        mThemes.filter { it.tag == tag }.first { return it }
        Log.i(LOG_TAG, "theme with tag $tag not found")
        return null
    }

    /**
     * Looks for theme with specified style resource id in themes list.
     * Returns theme if founded, or null.
     *
     * @param styleResID style resource id to find
     * @return {@link ColorTheme} or null
     */
    fun getTheme(@StyleRes styleResID: Int): ColorTheme? {
        checkInit()
        mThemes.filter { it.styleResID == styleResID }.first { return it }
        Log.i(LOG_TAG, "theme with style resource id $styleResID not found")
        return null
    }

    /**
     * Looks for theme with active theme tag in themes list.
     * Returns theme if founded, or null.
     *
     * @return {@link ColorTheme} or null
     */
    fun getActiveTheme(): ColorTheme? = getTheme(mActiveThemeTag)

    /**
     * Returns saved after last theme change theme tag.
     *
     * @return {@link String} saved theme tag
     */
    fun getSavedThemeTag(): String = mPrefs.getString(PREFERENCE_KEY, PREFERENCE_NO_VALUE)

    /**
     * Checks for active theme and changes it to specified theme if needed.
     *
     * @param theme {@link ColorTheme} to set as active
     */
    fun changeTheme(theme: ColorTheme?) {
        checkInit()

        if (theme == null || !mThemes.contains(theme)) {
            Log.w(LOG_TAG, "changing theme error")
            return
        }

        if (theme.tag == getSavedThemeTag()) {
            return
        }

        Log.i(LOG_TAG, "changing theme to $theme")

        mActiveThemeTag = theme.tag
        mPrefs.edit().putString(PREFERENCE_KEY, mActiveThemeTag).apply()
    }

    /**
     * Checks for active theme and changes it to theme with specified tag if needed.
     *
     * @param tag {@link String} theme tag
     */
    fun changeTheme(tag: String) = changeTheme(getTheme(tag))

    /**
     * Checks for active theme and changes it to theme with specified style resource id if needed.
     *
     * @param styleResID theme style resource id
     */
    fun changeTheme(@StyleRes styleResID: Int) = changeTheme(getTheme(styleResID))

    /**
     * Checks for active theme and changes it to specified theme if needed.
     *
     * @param theme {@link THEME} to set as active
     */
    fun changeTheme(theme: THEME) = changeTheme(getTheme(theme.tag))

    private fun checkInit() {
        if (!mInitialized) {
            throw IllegalStateException("MultiThemer is not initialized!")
        }
    }

    private fun containThemeWithTag(tag: String): Boolean = mThemes.any { it.tag == tag }

    /**
     * Initializes MultiThemer with specified {@link Builder}.
     *
     * @param builder {@link Builder} instance
     */
    private fun install(builder: Builder) {
        if (builder.themes.isEmpty()) {
            throw IllegalStateException("MultiThemer themes list is empty!")
        }

        mPrefs = builder.prefs
        mThemes = builder.themes
        mAppIcon = builder.icon

        val savedThemeTag = getSavedThemeTag()
        if (savedThemeTag == PREFERENCE_NO_VALUE || !containThemeWithTag(savedThemeTag)) {
            Log.i(LOG_TAG, "theme with saved tag not found")
            Log.i(LOG_TAG, "saving tag '${builder.defaultTag}' as default")
            mActiveThemeTag = builder.defaultTag
            mPrefs.edit().putString(PREFERENCE_KEY, builder.defaultTag).apply()
        } else {
            Log.i(LOG_TAG, "restoring theme with saved tag '$savedThemeTag'")
            mActiveThemeTag = savedThemeTag
        }

        mInitialized = true

        if (BuildConfig.DEBUG) {
            val time = System.currentTimeMillis() - builder.startTime
            Log.d(LOG_TAG, "MultiThemer initialized in $time milliseconds to ${builder.application.packageName}")
        }
    }

    /**
     * Enumeration of predefined by library themes
     */
    enum class THEME(val tag: String, @StyleRes val styleResID: Int) {
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
        DEEP_ORANGE("Deep Orange", R.style.MultiThemer_DeepOrange),
        BROWN("Brown", R.style.MultiThemer_Brown),
        GREY("Grey", R.style.MultiThemer_Grey),
        BLUE_GREY("Blue Grey", R.style.MultiThemer_BlueGrey),
        BLACK("Black", R.style.MultiThemer_Black);

        fun toColorTheme(context: Context) = ColorTheme(context, tag, styleResID)

        override fun toString(): String {
            return "THEME { tag='$tag', styleResID=$styleResID }"
        }
    }

    /**
     * Initialization builder
     */
    class Builder(internal var application: Application) {
        internal var startTime: Long = System.currentTimeMillis()
        internal var themes: ArrayList<ColorTheme> = arrayListOf()
        internal var defaultTag: String = THEME.INDIGO.tag
        internal var icon: Bitmap? = null
        internal var prefs: SharedPreferences

        init {
            prefs = PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
        }

        /**
         * Adds {@link ColorTheme} to themes list and sets it as default by {@code isDefault} flag.
         *
         * @param theme     {@link ColorTheme} to add
         * @param isDefault flag to use theme as default
         * @return this {@link Builder}
         */
        fun addTheme(theme: ColorTheme, isDefault: Boolean = false): Builder {
            checkForDuplicates(theme)
            if (isDefault) {
                defaultTag = theme.tag
            }
            themes.add(theme)

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "theme $theme added to list")
            }
            return this
        }

        /**
         * Adds {@link THEME} to themes list and sets it as default by {@code isDefault} flag.
         *
         * @param theme     {@link THEME} to add
         * @param isDefault flag to use theme as default
         * @return this {@link Builder}
         */
        fun addTheme(theme: THEME, isDefault: Boolean = false): Builder {
            return addTheme(theme.toColorTheme(application.applicationContext), isDefault)
        }

        /**
         * Adds {@link ColorTheme} to themes list with specified tag and style resource id
         * and sets it as default by {@code isDefault} flag.
         *
         * @param tag             {@link String} theme tag
         * @param styleResID theme style resource id
         * @param isDefault       flag to use theme as default
         * @return this {@link Builder}
         */
        fun addTheme(tag: String, @StyleRes styleResID: Int, isDefault: Boolean = false): Builder {
            val theme = ColorTheme(application.applicationContext, tag, styleResID)
            return addTheme(theme, isDefault)
        }

        /**
         * Sets specified {@link THEME} to use as default theme.
         *
         * @param theme {@link THEME}
         * @return this {@link Builder}
         */
        fun setDefault(theme: THEME): Builder {
            defaultTag = theme.tag
            return this
        }

        /**
         * Sets specified drawable resource id to use as app icon in recent apps list.
         * Doesn't affect Android SDK below 21.
         *
         * @param iconResId drawable resource id to use as app icon
         * @return this {@link Builder}
         */
        fun useAppIcon(@DrawableRes iconResId: Int): Builder {
            icon = BitmapFactory.decodeResource(application.resources, iconResId)
            return this
        }

        /**
         * Sets specified {@link Bitmap} to use as app icon in recent apps list.
         * Doesn't affect Android SDK below 21.
         *
         * @param iconBitmap {@link Bitmap} to use as app icon
         * @return this {@link Builder}
         */
        fun useAppIcon(iconBitmap: Bitmap): Builder {
            icon = iconBitmap
            return this
        }

        /**
         * Sets specified {@link SharedPreferences} that will be used to save theme tag after theme change.
         *
         * @param prefs {@link SharedPreferences} instance
         * @return this {@link Builder}
         */
        fun setSharedPreferences(prefs: SharedPreferences): Builder {
            this.prefs = prefs
            return this
        }

        /**
         * Method to initialize {@link MultiThemer} instance.
         * Will set {@link THEME#INDIGO} as default theme if saved tag won't found.
         */
        fun initialize() {
            if (themes.isEmpty()) {
                Log.i(LOG_TAG, "no themes was added, initializing with default themes list")
                THEME.values().forEach { addTheme(it) }
                if (themes.none { it.tag == defaultTag }) {
                    defaultTag = themes.first().tag
                }
            }
            MultiThemer.instance.install(this)
        }

        private fun checkForDuplicates(theme: ColorTheme) {
            if (themes.any { it.tag == theme.tag }) {
                throw IllegalStateException("theme with tag '${theme.tag}' already in the list")
            }
            if (themes.any { it.styleResID == theme.styleResID }) {
                throw IllegalStateException("theme with style resource id '${theme.styleResID}' already in the list")
            }
        }
    }
}
