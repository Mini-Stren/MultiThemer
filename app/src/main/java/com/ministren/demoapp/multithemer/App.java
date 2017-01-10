package com.ministren.demoapp.multithemer;

import android.app.Application;

import com.ministren.multithemer.MultiThemer;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        MultiThemer.setDebug(true);

        installDefaultsWithIcon();
//        installDefaultsWithoutIcon();
//        installDefaultsWithRed();
//        installCustomList();
    }

    private void installDefaultsWithIcon() {
        MultiThemer.build(this)
                .useAppIcon(R.mipmap.ic_launcher)
                .initialize();
    }

    private void installDefaultsWithoutIcon() {
        MultiThemer.build(this)
                .initialize();
    }

    private void installDefaultsWithRed() {
        MultiThemer.build(this)
                .useAppIcon(R.mipmap.ic_launcher)
                .setDefault(MultiThemer.THEME.RED)
                .initialize();
    }

    private void installCustomList() {
        MultiThemer.build(this)
                .useAppIcon(R.mipmap.ic_launcher)
                .addTheme(MultiThemer.THEME.BLUE, true)
                .addTheme(MultiThemer.THEME.PURPLE)
                .addTheme("Test theme", R.style.TestTheme)
                .initialize();
    }
}
