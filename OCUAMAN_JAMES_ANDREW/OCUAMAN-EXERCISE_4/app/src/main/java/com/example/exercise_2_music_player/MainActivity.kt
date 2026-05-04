package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val drawerNav = findViewById<NavigationView>(R.id.drawerNavigationView)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        setSupportActionBar(toolbar)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.spotify_text_primary))
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        val navController = navHost.navController

        bottomNav.setupWithNavController(navController)

        drawerNav.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.drawer_now_playing -> navController.navigate(R.id.myMusicFragment)
                R.id.drawer_profile -> navController.navigate(R.id.profileFragment)
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val showBottomNav = destination.id == R.id.myMusicFragment || destination.id == R.id.favoritesFragment

            bottomNav.visibility = if (showBottomNav) android.view.View.VISIBLE else android.view.View.GONE

            when (destination.id) {
                R.id.profileFragment -> {
                    drawerNav.setCheckedItem(R.id.drawer_profile)
                    supportActionBar?.title = getString(R.string.menu_profile)
                }

                R.id.myMusicFragment -> {
                    drawerNav.setCheckedItem(R.id.drawer_now_playing)
                    supportActionBar?.title = getString(R.string.menu_now_playing)
                }

                R.id.favoritesFragment -> {
                    drawerNav.setCheckedItem(R.id.drawer_now_playing)
                    supportActionBar?.title = getString(R.string.menu_favorites)
                }
            }
        }

        if (savedInstanceState == null) {
            drawerNav.setCheckedItem(R.id.drawer_now_playing)
        }
    }

    override fun onDestroy() {
        if (isFinishing) {
            SharedMusicPlayer.release()
        }
        super.onDestroy()
    }
}
