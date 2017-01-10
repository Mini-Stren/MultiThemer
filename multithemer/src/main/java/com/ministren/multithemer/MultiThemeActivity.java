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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        MultiThemer.getInstance().applyTheme(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MultiThemer.getInstance().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MultiThemer.getInstance().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(MultiThemer.PREFERENCE_KEY)) {
            Log.d(Utils.LOG_TAG, "theme change detected in activity " + toString());
            if (Build.VERSION.SDK_INT >= 11) {
                recreate();
            } else {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }
}
