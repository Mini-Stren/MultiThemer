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
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.ministren.multithemer.MultiThemer.Builder
import com.ministren.multithemer.MultiThemer.THEME
import com.ministren.multithemer.MultiThemer.build
import com.ministren.multithemer.MultiThemer.getActiveTheme
import com.ministren.multithemer.MultiThemer.getThemesList
import com.google.android.material.R as MaterialR

/**
 * Provides easy way to use as many app themes as you would like.
 * Will save last used application theme and restore it in app launch.
 *
 * All you need to do is:
 *
 * 1. Extend activity from [MultiThemeActivity] to let it
 * automatically apply application active theme and restart after theme change.
 *
 * 2. Define your themes in application resources, or use up to 20 predefined themes in [THEME].
 *
 * 3. Initialize [MultiThemer] in your [Application] class using method [build].
 *    1. [build] will return [Builder],
 *  which you can use to add themes, set icon to use in recent apps list,
 *  set SharedPreferences that will be used to save theme tag after theme change
 *  and set by default one of [THEME] themes.
 *    2. If you will not use any of [Builder.addTheme] methods,
 *  it will automatically fill themes list with all predefined [THEME] themes.
 *  It will use [THEME.INDIGO] as default theme,
 *  or you can change it with [Builder.setDefault].
 *    3. While adding your own themes using one of [Builder.addTheme] methods,
 *  don't forget to set `isDefault = true` for at least one of them,
 *  otherwise the first theme in the list will be used by default one.
 *    4. [Builder.setSharedPreferences] is optional.
 *  [PreferenceManager.getDefaultSharedPreferences] will be used by default.
 *
 * You can use [MultiThemerListFragment] to present all application themes
 * and give users easy way to switch between themes,
 * or use [getThemesList] and [getActiveTheme] to write your own theme chooser.
 *
 * Look at [GitHub repository](https://github.com/Mini-Stren/MultiThemer) for more info, screenshots and demo app.
 *
 * @see Builder.useAppIcon
 * @see THEME
 * @see ColorTheme
 * @see MultiThemeActivity
 *
 * Created by Mini-Stren on 28.08.2017.
 */
public object MultiThemer {

    internal const val LOG_TAG = "MultiThemer"
    internal const val PREFERENCE_KEY = "com.ministren.multithemer.SAVED_TAG"
    private const val PREFERENCE_NO_VALUE = "com.ministren.multithemer.NO_SAVED_TAG_VALUE"

    private lateinit var themesList: List<ColorTheme>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var activeThemeTag: String

    @DrawableRes
    private var appIconResId: Int? = null
    private var initialized = false

    /**
     * Creates a builder instance for this class to initialize library.
     *
     * @param application [Application] to attach to
     * @return [Builder] to initialize the library with
     */
    public fun build(application: Application): Builder = Builder(application)

    /**
     * Sets active theme to specified activity.
     *
     * [MultiThemeActivity] calls this method in [MultiThemeActivity.onCreate]
     *
     * @param activity [Activity] to set theme
     */
    public fun applyThemeTo(activity: Activity) {
        val startTime = System.currentTimeMillis()
        checkInit()

        val activeTheme = getActiveTheme()
        activity.setTheme(activeTheme!!.styleResID)

        if (appIconResId != null) {
            val primaryColor = activeTheme.getAttrColor(MaterialR.attr.colorPrimary)
            val taskDescription = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityManager.TaskDescription.Builder().apply {
                    appIconResId?.let(::setIcon)
                    setPrimaryColor(primaryColor)
                }.build()
            } else {
                val appIcon = appIconResId?.let {
                    BitmapFactory.decodeResource(activity.resources, it)
                }
                @Suppress("DEPRECATION")
                ActivityManager.TaskDescription(null, appIcon, primaryColor)
            }
            activity.setTaskDescription(taskDescription)
        }

        if (BuildConfig.DEBUG) {
            val time = System.currentTimeMillis() - startTime
            Log.d(LOG_TAG, "theme applied in $time milliseconds to activity $activity")
        }
    }

    public fun getThemesList(): List<ColorTheme> {
        checkInit()
        return themesList
    }

    public fun getSharedPreferences(): SharedPreferences {
        checkInit()
        return sharedPreferences
    }

    /**
     * Looks for theme with specified tag in themes list.
     * Returns theme if founded, or null.
     *
     * @param tag theme tag to find
     * @return [ColorTheme] or null
     */
    public fun getTheme(tag: String): ColorTheme? {
        checkInit()
        return themesList.firstOrNull { it.tag == tag } ?: null.also {
            Log.i(LOG_TAG, "theme with tag '$tag' not found")
        }
    }

    /**
     * Looks for theme with specified style resource id in themes list.
     * Returns theme if founded, or null.
     *
     * @param styleResID style resource id to find
     * @return [ColorTheme] or null
     */
    public fun getTheme(@StyleRes styleResID: Int): ColorTheme? {
        checkInit()
        return themesList.firstOrNull { it.styleResID == styleResID } ?: null.also {
            Log.i(LOG_TAG, "theme with style resource id '$styleResID' not found")
        }
    }

    /**
     * Looks for theme with active theme tag in themes list.
     * Returns theme if founded, or null.
     *
     * @return [ColorTheme] or null
     */
    public fun getActiveTheme(): ColorTheme? {
        checkInit()
        return getTheme(activeThemeTag)
    }

    /**
     * Returns saved after last theme change theme tag.
     *
     * @return saved theme tag
     */
    public fun getSavedThemeTag(): String =
        getSharedPreferences().getString(PREFERENCE_KEY, PREFERENCE_NO_VALUE) ?: PREFERENCE_NO_VALUE

    /**
     * Checks for active theme and changes it to specified theme if needed.
     *
     * @param theme [ColorTheme] to set as active
     */
    public fun changeTheme(theme: ColorTheme?) {
        checkInit()

        if (theme == null || !themesList.contains(theme)) {
            Log.w(LOG_TAG, "can't change theme to $theme")
            return
        }

        if (theme.tag == getSavedThemeTag()) return

        Log.i(LOG_TAG, "changing theme to $theme")

        activeThemeTag = theme.tag
        sharedPreferences.edit { putString(PREFERENCE_KEY, activeThemeTag) }
    }

    /**
     * Checks for active theme and changes it to theme with specified tag if needed.
     *
     * @param tag theme tag
     */
    public fun changeTheme(tag: String) {
        changeTheme(getTheme(tag))
    }

    /**
     * Checks for active theme and changes it to theme with specified style resource id if needed.
     *
     * @param styleResID theme style resource id
     */
    public fun changeTheme(@StyleRes styleResID: Int) {
        changeTheme(getTheme(styleResID))
    }

    /**
     * Checks for active theme and changes it to specified theme if needed.
     *
     * @param theme [THEME] to set as active
     */
    public fun changeTheme(theme: THEME) {
        changeTheme(getTheme(theme.tag))
    }

    private fun checkInit() {
        if (!initialized) error("MultiThemer is not initialized!")
    }

    /**
     * Initializes MultiThemer with specified [Builder].
     *
     * @param builder [Builder] instance
     */
    private fun install(builder: Builder) {
        if (builder.themesList.isEmpty()) {
            error("MultiThemer themes list is empty!")
        }

        sharedPreferences = builder.sharedPreferences
        themesList = builder.themesList
        appIconResId = builder.appIconResId

        val savedThemeTag = sharedPreferences.getString(PREFERENCE_KEY, PREFERENCE_NO_VALUE)
            ?: PREFERENCE_NO_VALUE
        val containThemeWithTag = themesList.any { it.tag == savedThemeTag }
        if (savedThemeTag == PREFERENCE_NO_VALUE || !containThemeWithTag) {
            Log.i(LOG_TAG, "theme with saved tag not found")
            Log.i(LOG_TAG, "saving tag '${builder.defaultThemeTag}' as default")
            activeThemeTag = builder.defaultThemeTag
            sharedPreferences.edit { putString(PREFERENCE_KEY, builder.defaultThemeTag) }
        } else {
            Log.i(LOG_TAG, "restoring theme with saved tag '$savedThemeTag'")
            activeThemeTag = savedThemeTag
        }

        initialized = true

        if (BuildConfig.DEBUG) {
            val time = System.currentTimeMillis() - builder.initStartTime
            Log.d(
                LOG_TAG,
                "MultiThemer initialized in $time milliseconds to ${builder.application.packageName}"
            )
        }
    }

    /**
     * Enumeration of predefined by library themes
     */
    public enum class THEME(
        public val tag: String,
        @StyleRes public val styleResID: Int,
    ) {
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

        public fun toColorTheme(context: Context): ColorTheme = ColorTheme(context, tag, styleResID)

        override fun toString(): String = "THEME { tag='$tag', styleResID='$styleResID' }"
    }

    /**
     * Initialization builder
     */
    public class Builder(internal val application: Application) {
        internal val initStartTime: Long = System.currentTimeMillis()

        internal val themesList = mutableListOf<ColorTheme>()
        internal var defaultThemeTag: String = THEME.INDIGO.tag

        @DrawableRes
        internal var appIconResId: Int? = null
        internal var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

        /**
         * Adds [ColorTheme] to themes list and sets it as default by [isDefault] flag.
         *
         * @param theme [ColorTheme] to add
         * @param isDefault flag to use theme as default
         * @return this [Builder]
         */
        @JvmOverloads
        public fun addTheme(
            theme: ColorTheme,
            isDefault: Boolean = false,
        ): Builder {
            checkForDuplicates(theme)
            if (isDefault) {
                defaultThemeTag = theme.tag
            }
            themesList.add(theme)

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "theme $theme added to list")
            }
            return this
        }

        /**
         * Adds [THEME] to themes list and sets it as default by [isDefault] flag.
         *
         * @param theme [THEME] to add
         * @param isDefault flag to use theme as default
         * @return this [Builder]
         */
        @JvmOverloads
        public fun addTheme(
            theme: THEME,
            isDefault: Boolean = false,
        ): Builder = addTheme(theme.toColorTheme(application), isDefault)

        /**
         * Adds [ColorTheme] to themes list with specified tag and style resource id
         * and sets it as default by [isDefault] flag.
         *
         * @param tag theme tag
         * @param styleResID theme style resource id
         * @param isDefault flag to use theme as default
         * @return this [Builder]
         */
        @JvmOverloads
        public fun addTheme(
            tag: String,
            @StyleRes styleResID: Int,
            isDefault: Boolean = false,
        ): Builder = addTheme(ColorTheme(application, tag, styleResID), isDefault)

        /**
         * Sets specified [THEME] to use as default theme.
         *
         * @param theme [THEME]
         * @return this [Builder]
         */
        public fun setDefault(theme: THEME): Builder {
            defaultThemeTag = theme.tag
            return this
        }

        /**
         * Sets specified drawable resource id to use as app icon in recent apps list.
         *
         * @param iconResId drawable resource id to use as app icon
         * @return this [Builder]
         */
        public fun useAppIcon(@DrawableRes iconResId: Int): Builder {
            this.appIconResId = iconResId
            return this
        }

        /**
         * Sets specified [SharedPreferences] that will be used to save theme tag after theme change.
         *
         * @param prefs [SharedPreferences] instance
         * @return this [Builder]
         */
        public fun setSharedPreferences(prefs: SharedPreferences): Builder {
            this.sharedPreferences = prefs
            return this
        }

        /**
         * Method to initialize [MultiThemer] instance.
         * Will set [THEME.INDIGO] as default theme if saved tag won't found.
         */
        public fun initialize() {
            if (themesList.isEmpty()) {
                Log.i(LOG_TAG, "no themes was added, initializing with default themes list")
                THEME.entries.forEach(::addTheme)
            }
            if (themesList.none { it.tag == defaultThemeTag }) {
                Log.i(
                    LOG_TAG,
                    "theme with default tag not found in themes list, setting first theme as default"
                )
                defaultThemeTag = themesList.first().tag
            }
            install(this)
        }

        private fun checkForDuplicates(theme: ColorTheme) {
            if (themesList.any { it.tag == theme.tag }) {
                error("Theme with tag '${theme.tag}' already in the list.")
            }
            if (themesList.any { it.styleResID == theme.styleResID }) {
                error("Theme with style resource id '${theme.styleResID}' already in the list.")
            }
        }
    }
}
