package com.ministren.demoapp.multithemer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.ministren.multithemer.MultiThemeActivity;
import com.ministren.multithemer.MultiThemerListFragment;

public class MainActivity extends MultiThemeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addPage(new ExamplesFragment(), "Examples");
        adapter.addPage(new MultiThemerListFragment(), "MultiThemer List Fragment");

        ViewPager viewPager = (ViewPager) findViewById(R.id.main_view_pager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(1).select();
    }
}
