package com.ministren.demoapp.multithemer

import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ministren.multithemer.MultiThemeActivity
import com.ministren.multithemer.MultiThemerListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MultiThemeActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private var mFragmentsList = arrayListOf(ExamplesFragment(), MultiThemerListFragment())

        override fun getItem(position: Int): Fragment {
            return mFragmentsList[position]
        }

        override fun getCount(): Int {
            return mFragmentsList.size
        }
    }

    class ExamplesFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            return inflater?.inflate(R.layout.fragment_examples, container, false)
        }
    }
}
