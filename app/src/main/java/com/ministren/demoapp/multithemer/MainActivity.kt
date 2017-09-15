package com.ministren.demoapp.multithemer

import android.content.Intent
import android.net.Uri
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.*
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_github -> {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.addCategory("android.intent.category.BROWSABLE")
                intent.data = Uri.parse("https://github.com/Mini-Stren/MultiThemer")
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
