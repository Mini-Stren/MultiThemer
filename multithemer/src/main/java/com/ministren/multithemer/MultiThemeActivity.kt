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
import android.util.Log
import androidx.annotation.CallSuper
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * This activity uses [MultiThemer] to automatically
 * apply application active theme and restart itself after theme change
 *
 * Created by Mini-Stren on 28.08.2017.
 */
public open class MultiThemeActivity : AppCompatActivity,
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var activeThemeTag: String

    public constructor() : super()

    @ContentView
    public constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        MultiThemer.applyThemeTo(this)
        activeThemeTag = MultiThemer.getSavedThemeTag()
        super.onCreate(savedInstanceState)
    }

    @CallSuper
    protected override fun onPause() {
        super.onPause()
        MultiThemer.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
    }

    @CallSuper
    protected override fun onResume() {
        super.onResume()
        MultiThemer.getSharedPreferences().registerOnSharedPreferenceChangeListener(this)
        val tag = MultiThemer.getSavedThemeTag()
        if (activeThemeTag != tag) restartActivity()
    }

    @CallSuper
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
