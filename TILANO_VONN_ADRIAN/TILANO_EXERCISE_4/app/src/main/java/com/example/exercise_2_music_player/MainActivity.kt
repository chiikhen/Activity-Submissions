package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI // ADDED THIS IMPORT
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), SelectedSongListener {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout

    private var currentIndex = 0

    private val songs = listOf(
        "Song 1" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Initialize Navigation Components
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        drawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // 2. Setup NavController with UI
        navView.setupWithNavController(navController)
        bottomNav.setupWithNavController(navController)
        setupActionBarWithNavController(navController, drawerLayout)
    }

    override fun onSupportNavigateUp(): Boolean {
        // FIXED: Using NavigationUI to handle the drawer and back navigation correctly
        return NavigationUI.navigateUp(navController, drawerLayout) || super.onSupportNavigateUp()
    }

    // --- Interface Methods ---

    override fun onSongSelected(index: Int) {
        currentIndex = index

        // Navigate to the player screen
        navController.navigate(R.id.manageSong)

        // Using a listener to wait until the fragment is actually swapped
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.manageSong) {
                // Give the fragment a millisecond to "settle" in the UI thread
                window.decorView.post {
                    updatePlayerFragment()
                }
            }
        }
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
        // Find the NavHost and then look for the fragment currently on stage
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull { it is ManageSong } as? ManageSong

        if (currentFragment != null) {
            val (songTitle, songUrl) = songs[currentIndex]
            currentFragment.loadNewSong(songTitle, songUrl)
        }
    }
}