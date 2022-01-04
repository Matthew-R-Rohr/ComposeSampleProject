package com.fct.compose.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import com.fct.compose.R
import com.fct.compose.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNav(savedInstanceState)
    }

    /**
     * Navigation needs to be manually setup to account for the various launch data tier fragments
     */
    private fun setupNav(savedInstanceState: Bundle?) {

        // set fragment for selected
        binding.navView.setOnNavigationItemSelectedListener {
            doFragmentSwap(
                when (it.itemId) {
                    R.id.navigation_launches -> LaunchListFragment()
                    R.id.navigation_debug -> DebugFragment()
                    else -> LaunchListFragment()
                }
            )
            true
        }

        // set initially loaded fragment if not already set
        if (savedInstanceState == null) doFragmentSwap(LaunchListFragment() as Fragment)
    }


    private fun doFragmentSwap(fragment: Fragment) = supportFragmentManager.commitNow {
        replace(R.id.nav_host_fragment, fragment)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_right)
    }
}