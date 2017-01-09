package com.ministren.demoapp.multithemer;

import android.app.Application;

import com.ministren.multithemer.ColorTheme;
import com.ministren.multithemer.MultiThemer;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        installDefault();
//        installDefaultRed();
//        installTestTheme();

        MultiThemer.useAppIcon(this, R.mipmap.ic_launcher);
    }

    private void installDefault() {
        MultiThemer.install(this);
    }

    private void installDefaultRed() {
        MultiThemer.install(this, MultiThemer.THEME.RED);
    }

    private void installTestTheme() {
        List<ColorTheme> colorThemesList = new ArrayList<>();
        colorThemesList.add(new ColorTheme(this, "Test Theme", R.style.TestTheme));
        MultiThemer.install(this, colorThemesList, "Test Theme");
    }
}
