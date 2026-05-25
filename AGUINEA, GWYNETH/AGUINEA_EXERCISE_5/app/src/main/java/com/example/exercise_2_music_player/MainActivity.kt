package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity(), MusicPlayerListener {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )
    
    private var currentIndex = -1
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup AppBarConfiguration with the set of top-level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.songListFragment, R.id.favoritesFragment, R.id.profileFragment),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)
        
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onSongSelected(song: String, index: Int) {
        currentIndex = index
        playCurrentSong()
    }

    override fun onNextSong() {
        if (songs.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % songs.size
            playCurrentSong()
        }
    }

    override fun onPreviousSong() {
        if (songs.isNotEmpty()) {
            currentIndex = if (currentIndex <= 0) songs.size - 1 else currentIndex - 1
            playCurrentSong()
        }
    }

    private fun playCurrentSong() {
        if (currentIndex in songs.indices) {
            val playerFragment = supportFragmentManager.findFragmentById(R.id.player_container) as? MusicPlayerFragment
            playerFragment?.playSong(songs[currentIndex])
        }
    }
}