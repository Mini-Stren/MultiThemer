package com.ministren.demoapp.multithemer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentsList = new ArrayList<>();
    private final List<String> mFragmentsTitleList = new ArrayList<>();

    ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    void addPage(Fragment pageFragment, String pageTitle) {
        mFragmentsList.add(pageFragment);
        mFragmentsTitleList.add(pageTitle);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentsList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentsTitleList.get(position);
    }
}
