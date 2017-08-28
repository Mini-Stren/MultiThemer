package com.ministren.demoapp.multithemer

import android.app.Application
import com.ministren.multithemer.MultiThemer

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        installDefaultsWithIcon()
//        installDefaultsWithoutIcon()
//        installDefaultsWithRed()
//        installCustomList()
    }

    private fun installDefaultsWithIcon() {
        MultiThemer.build(this)
                .useAppIcon(R.mipmap.ic_launcher)
                .initialize()
    }

    private fun installDefaultsWithoutIcon() {
        MultiThemer.build(this)
                .initialize()
    }

    private fun installDefaultsWithRed() {
        MultiThemer.build(this)
                .useAppIcon(R.mipmap.ic_launcher)
                .setDefault(MultiThemer.THEME.RED)
                .initialize()
    }

    private fun installCustomList() {
        MultiThemer.build(this)
                .useAppIcon(R.mipmap.ic_launcher)
                .addTheme(MultiThemer.THEME.BLUE, true)
                .addTheme(MultiThemer.THEME.PURPLE)
                .addTheme("Test theme", R.style.TestTheme)
                .initialize()
    }
}
