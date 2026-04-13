package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), SelectedSongListener {

    private var currentIndex = 0
    private val songs = listOf(
        "Clarity" to "https://drive.google.com/uc?export=download&id=14j6dm05QQ91zp5vyClDgBbCmnOOezaTX",
        "Konsensya" to "https://drive.google.com/uc?export=download&id=1rQLvFQM-9J274cR0n5nsRsd89vpT5-ev",
        "Aura" to "https://drive.google.com/uc?export=download&id=1HkI9vgKPnMXussNjyKnO1NdcJNojlhlz",
        "Oldies Station" to "https://drive.google.com/uc?export=download&id=1s6isfqab1f1o04cUxSISKThI3yL8s-pN",
        "Mulberry Street" to "https://drive.google.com/uc?export=download&id=1R-q0M39YIKz7T_yYKPCbX8VT7g7cRJUc",
        "Drag Path" to "https://drive.google.com/uc?export=download&id=1GYXp6o5VhVtDXT_XFxWQ6xffUUJEU8aD",
        "All I Did Was Dream of You" to "https://drive.google.com/uc?export=download&id=1Xk-wc8844bu6IMa5rj1Qy6IDgL3aji8C"
    )

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController


        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_my_music, R.id.nav_favorites, R.id.nav_now_playing, R.id.nav_profile),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView = findViewById<NavigationView>(R.id.navView)
        navView.setupWithNavController(navController)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavView)
        bottomNav.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onSongSelected(index: Int) {
        currentIndex = index
        updatePlayerFragment()
    }

    override fun onNextSong() {
        currentIndex = (currentIndex + 1) % songs.size
        updatePlayerFragment()
    }

    override fun onPreviousSong() {
        currentIndex = if (currentIndex > 0) currentIndex - 1 else songs.size - 1
        updatePlayerFragment()
    }

    private fun updatePlayerFragment() {
        val playerFrag = supportFragmentManager
            .findFragmentById(R.id.player_container) as? ManageSong
        val (songTitle, songUrl) = songs[currentIndex]
        playerFrag?.loadNewSong(songTitle, songUrl)
    }
}