package com.ministren.demoapp.multithemer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.ministren.demoapp.multithemer.databinding.ActivityMainBinding
import com.ministren.multithemer.MultiThemeActivity
import com.ministren.multithemer.MultiThemerListFragment
import dev.androidbroadcast.vbpd.viewBinding

class MainActivity : MultiThemeActivity(R.layout.activity_main) {

    private val viewBinding by viewBinding(ActivityMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setSupportActionBar(viewBinding.toolbar)

        with(viewBinding) {
            container.adapter = SectionsPagerAdapter()
            TabLayoutMediator(tabs, container) { tab, position ->
                tab.setText(
                    when (position) {
                        0 -> R.string.tab_text_1
                        1 -> R.string.tab_text_2
                        else -> throw IllegalStateException()
                    }
                )
            }.attach()
        }

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { _, insets ->
            val systemInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            viewBinding.appbar.updatePaddingRelative(
                top = systemInsets.top,
                start = systemInsets.left,
                end = systemInsets.right,
            )
            viewBinding.container.updatePaddingRelative(
                bottom = systemInsets.bottom,
                start = systemInsets.left,
                end = systemInsets.right,
            )
            WindowInsetsCompat.CONSUMED
        }
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
                intent.data = "https://github.com/Mini-Stren/MultiThemer".toUri()
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class SectionsPagerAdapter : FragmentStateAdapter(this) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> ExamplesFragment()
            1 -> MultiThemerListFragment()
            else -> throw IllegalStateException()
        }
    }

    class ExamplesFragment : Fragment(R.layout.fragment_examples)
}
