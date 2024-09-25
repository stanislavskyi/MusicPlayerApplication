package com.hfad.musicplayerapplication.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hfad.musicplayerapplication.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        val navController = navHostFragment.navController


        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val topNavigation: MaterialToolbar = findViewById(R.id.topAppBar)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.menuCharter -> {
                    navController.navigate(R.id.charterFragment)
                    true
                }

                R.id.menuPremium -> {
                    navController.navigate(R.id.premiumFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }


        topNavigation.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.account -> {
                    topNavigation.visibility = View.GONE
                    bottomNavigation.visibility = View.GONE
                    navController.navigate(R.id.registerFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.registerFragment -> {
                    topNavigation.visibility = View.INVISIBLE
                    bottomNavigation.visibility = View.INVISIBLE
                }
                else -> {
                    bottomNavigation.visibility = View.VISIBLE
                    topNavigation.visibility = View.VISIBLE

                }
            }
        }
    }
}