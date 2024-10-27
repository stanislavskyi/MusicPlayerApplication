package com.hfad.musicplayerapplication.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.entity.Track
import com.hfad.musicplayerapplication.presentation.screens.MusicPlayerFragment
import com.hfad.musicplayerapplication.presentation.services.MiniPlayerFragment
import com.hfad.musicplayerapplication.presentation.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    //private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var sharedViewModel: SharedViewModel

    private fun showMiniPlayerFragment(music: Track) {

        sharedViewModel = ViewModelProvider(this@MainActivity)[SharedViewModel::class.java]
        sharedViewModel.updateTrack(music)
//
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
//        val navController = navHostFragment.navController
//
//        val currentFragment = navController.currentDestination
//
//        if( !(currentFragment?.label.toString() == "fragment_music_player") ){
//            val existingFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_mini_player)
//            if (existingFragment == null) {
//                val fragment = MiniPlayerFragment()
//                supportFragmentManager.beginTransaction()
//                    .add(R.id.fragment_container_mini_player, fragment)
//                    .commit()
//            }
//        }

        val existingFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_mini_player)
        //if (existingFragment == null) {
            val fragment = MiniPlayerFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_mini_player, fragment)
                .commit()
    //    }
    }

    private val musicPlayerReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra("TRACK_URI", Track::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent?.getParcelableExtra("TRACK_URI")
            }

            showMiniPlayerFragment(track!!)
        }
    }

    fun showFragment() {

        val fragmentB = MusicPlayerFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_down)
            .replace(R.id.main_container, fragmentB)
            .replace(R.id.fragment_container_mini_player, Fragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(musicPlayerReceiver, IntentFilter("MUSIC_PLAYER_ACTION"))

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

                R.id.menuMediaLibrary -> {
                    navController.navigate(R.id.libraryFragment)
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
    }
}