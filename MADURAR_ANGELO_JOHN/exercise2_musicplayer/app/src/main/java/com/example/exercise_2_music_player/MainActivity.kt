package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Get NavController from NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        drawerLayout = findViewById(R.id.drawerLayout)

        // Tell AppBarConfiguration which destinations are "top-level"
        // (no back arrow on these; hamburger icon shows instead)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.myMusicFragment,
                R.id.favoritesFragment,
                R.id.nowPlayingFragment,
                R.id.profileFragment
            ),
            drawerLayout
        )

        // Wire Toolbar with NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Wire Drawer with NavController
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        NavigationUI.setupWithNavController(navigationView, navController)

        // Wire Bottom Navigation with NavController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        NavigationUI.setupWithNavController(bottomNav, navController)
    }

    // Handles the hamburger/back button in the toolbar
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}