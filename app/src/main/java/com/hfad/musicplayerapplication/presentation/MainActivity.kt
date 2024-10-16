package com.hfad.musicplayerapplication.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hfad.musicplayerapplication.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.musicPlayerFragment -> {
//                    topNavigation.visibility = View.INVISIBLE
//                    bottomNavigation.visibility = View.INVISIBLE
//                }
//                else -> {
//                    bottomNavigation.visibility = View.VISIBLE
//                    topNavigation.visibility = View.VISIBLE
//
//                }
//            }
//        }
    }

    private fun showNotification(){
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =  NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        //(выше) для android 8, начиная включительно с API 26

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Title")
            .setContentText("Text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()


        notificationManager.notify(1, notification)
    }

    companion object{
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
    }
}