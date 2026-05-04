package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.exercise_2_music_player.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MusicPlayerListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var currentPosition: Int = 0
    private lateinit var songs: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Define top-level destinations (no back button, drawer icon instead)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.musicListFragment, R.id.favoritesFragment, R.id.profileFragment),
            binding.drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        binding.bottomNavigation.setupWithNavController(navController)

        songs = listOf(
            "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onSongSelected(position: Int) {
        currentPosition = position
        playCurrentSong()
    }

    override fun onNextRequested() {
        if (currentPosition < songs.size - 1) {
            currentPosition++
            playCurrentSong()
        }
    }

    override fun onPreviousRequested() {
        if (currentPosition > 0) {
            currentPosition--
            playCurrentSong()
        }
    }

    private fun playCurrentSong() {
        val songData = songs[currentPosition]
        val parts = songData.split(" - ")
        if (parts.size == 2) {
            val title = parts[0]
            val url = parts[1]
            val playerFragment = supportFragmentManager.findFragmentById(R.id.player_container) as? MusicPlayerFragment
            playerFragment?.playSong(title, url)
        }
    }
}
