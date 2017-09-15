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

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * * Activity use {@link MultiThemer} to automatically
 * apply app's active theme and restart itself after theme change
 * <p>
 *
 * Created by Mini-Stren on 28.08.2017.
 */

open class MultiThemeActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mThemeTag: String

    override fun onCreate(savedInstanceState: Bundle?) {
        MultiThemer.applyThemeTo(this)
        mThemeTag = MultiThemer.getSavedThemeTag()
        super.onCreate(savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        MultiThemer.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onResume() {
        super.onResume()
        MultiThemer.getSharedPreferences().registerOnSharedPreferenceChangeListener(this)
        val tag = MultiThemer.getSavedThemeTag()
        if (mThemeTag != tag) restartActivity()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == MultiThemer.PREFERENCE_KEY) restartActivity()
    }

    private fun restartActivity() {
        if (BuildConfig.DEBUG) {
            Log.d(MultiThemer.LOG_TAG, "restarting activity '$this'")
        }
        recreate()
    }
}
