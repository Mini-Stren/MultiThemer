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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Activity use {@link MultiThemer} to automatically
 * apply app's active theme and restart itself after theme change
 * <p>
 * Created by Mini-Stren on 06.01.2017
 */

public class MultiThemeActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private String themeTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        MultiThemer.getInstance().applyTheme(this);
        themeTag = MultiThemer.getInstance().getSavedTag();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MultiThemer.getInstance().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MultiThemer.getInstance().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        String tag = MultiThemer.getInstance().getSavedTag();
        if (!themeTag.equals(tag)) {
            restartActivity();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(MultiThemer.PREFERENCE_KEY)) {
            restartActivity();
        }
    }

    public void restartActivity() {
        if (BuildConfig.DEBUG) {
            Log.d(MultiThemer.LOG_TAG, "restarting activity " + toString());
        }
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        } else {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}
