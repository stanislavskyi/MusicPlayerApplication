package com.hfad.musicplayerapplication.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.entity.Track
import com.hfad.musicplayerapplication.presentation.screens.LoginFragment
import com.hfad.musicplayerapplication.presentation.screens.MusicPlayerFragment
import com.hfad.musicplayerapplication.presentation.screens.RegisterFragment
import com.hfad.musicplayerapplication.presentation.services.MiniPlayerFragment
import com.hfad.musicplayerapplication.presentation.services.MusicPlayerService
import com.hfad.musicplayerapplication.presentation.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    //private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var topNavigation: MaterialToolbar
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var navHostFragment: NavHostFragment

    private var userId: String? = null

    private fun showMiniPlayerFragment(music: Track) {

        sharedViewModel = ViewModelProvider(this@MainActivity)[SharedViewModel::class.java]
        sharedViewModel.updateTrack(music)


        supportFragmentManager.findFragmentById(R.id.fragment_container_mini_player)
            val fragment = MiniPlayerFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container_mini_player, fragment)
                .commit()

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getStringExtra("open_fragment") == "MusicPlayerFragment") {
            openMusicPlayerFragment()
        }
    }

    // Функция для открытия MusicPlayerFragment
    private fun openMusicPlayerFragment() {
        //for notifications
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, MusicPlayerFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        checkCurrentFragmentAndToggleNavigation()
    }

    private fun checkCurrentFragmentAndToggleNavigation() {
        val currentFragment = navHostFragment.childFragmentManager.fragments.getOrNull(0)
        if (currentFragment !is RegisterFragment && currentFragment !is LoginFragment) {
            topNavigation.visibility = View.VISIBLE
            bottomNavigation.visibility = View.VISIBLE
        } else {
            topNavigation.visibility = View.GONE
            bottomNavigation.visibility = View.GONE
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null){
            navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
            navHostFragment.navController.navigate(R.id.registerFragment)
        }





        if(intent.getStringExtra("open_fragment") == "MusicPlayerFragment"){
            openMusicPlayerFragment()
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout) // Предположим, что у вас есть DrawerLayout
        val navigationView: NavigationView = findViewById(R.id.navigation_view)


        LocalBroadcastManager.getInstance(this)
            .registerReceiver(musicPlayerReceiver, IntentFilter("MUSIC_PLAYER_ACTION"))

        navHostFragment = supportFragmentManager.findFragmentById(R.id.main_container) as NavHostFragment
        val navController = navHostFragment.navController


        bottomNavigation = findViewById(R.id.bottom_navigation)
        topNavigation = findViewById(R.id.topAppBar)

        Firebase.firestore.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                if (document.id == userId){
                    val name = document.getString("name")?.substringBefore("@gmail.com", "")
                    topNavigation.setTitle("$name")
                }
            }
        }


        //

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    checkCurrentFragmentAndToggleNavigation()
                }
            },
            true
        )

        //

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
                    when {
                        userId == null -> {
                            navController.navigate(R.id.registerFragment)
                        }
                        userId != null -> {
                            navController.navigate(R.id.accoutnFragment)
                        }
                    }
                    true
                }
                else -> {
                    true
                }
            }
        }

        topNavigation.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_item_1 -> {
                    // Обработка нажатия на Item 1
                    val anchorView = navigationView.findViewById<View>(R.id.nav_item_1) // Используйте Item, на который нажали
                    showPopupMenu(anchorView)
                    true
                }
                else -> false
            }
        }

    }

    private fun showPopupMenu(anchorView: View) {
        // Используйте меню, привязанное к элементу, по которому было выполнено нажатие
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(R.menu.language_menu, popupMenu.menu)

        // Устанавливаем обработчики кликов для элементов меню
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.lang_item_1 -> {
                    Toast.makeText(this, "Option 1 clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.lang_item_2 -> {
                    Toast.makeText(this, "Option 2 clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // Показать меню
        popupMenu.show()
    }




    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MusicPlayerService::class.java))
    }
}