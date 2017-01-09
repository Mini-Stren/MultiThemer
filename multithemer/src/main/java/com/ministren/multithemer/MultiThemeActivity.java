package com.ministren.multithemer;

import android.content.Intent;
import android.content.SharedPreferences;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        MultiThemer.applyTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.getAppPrefs(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.getAppPrefs(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Utils.PREFERENCE_KEY)) {
            Log.d(Utils.LOG_TAG, "theme change detected, restarting activity " + toString());
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}
