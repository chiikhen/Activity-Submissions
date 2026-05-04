package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), MusicPlayerInterface {

    private val songs = listOf(
        "Ambient Dreams|Relaxing Music - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Electric Vibes|Electronic Mix - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Jazz Cafe|Smooth Jazz - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "Summer Breeze|Chill Beats - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        "Night Drive|Lo-Fi Hip Hop - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
        "Ocean Waves|Nature Sounds - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3"
    )
    private val favoriteSongs = mutableListOf(
        songs[0],
        songs[2],
        songs[4]
    )

    private var currentSongIndex: Int = 0
    private lateinit var musicPlayerFragment: MusicPlayerFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val mainContent = findViewById<View>(R.id.main)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            mainContent.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right
            )
            navigationView.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            bottomNavigationView.updatePadding(bottom = systemBars.bottom)
            insets
        }

        setupNavigation(drawerLayout, navigationView, bottomNavigationView)
        setupMusicPlayerFragment()
    }

    private fun setupNavigation(
        drawerLayout: DrawerLayout,
        navigationView: NavigationView,
        bottomNavigationView: BottomNavigationView
    ) {
        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topAppBar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nowPlayingFragment,
                R.id.profileFragment,
                R.id.myMusicFragment,
                R.id.favoritesFragment
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setupMusicPlayerFragment() {
        val existingFragment =
            supportFragmentManager.findFragmentById(R.id.musicPlayerContainer) as? MusicPlayerFragment

        if (existingFragment != null) {
            musicPlayerFragment = existingFragment
            return
        }

        musicPlayerFragment = MusicPlayerFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.musicPlayerContainer, musicPlayerFragment)
            .commitNow()
    }

    override fun onSongSelected(songIndex: Int) {
        if (songIndex >= 0 && songIndex < songs.size) {
            currentSongIndex = songIndex
            musicPlayerFragment.loadAndPlaySong(songIndex)
        }
    }

    override fun onPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--
            musicPlayerFragment.loadAndPlaySong(currentSongIndex)
        }
    }

    override fun onNextSong() {
        if (currentSongIndex < songs.size - 1) {
            currentSongIndex++
            musicPlayerFragment.loadAndPlaySong(currentSongIndex)
        }
    }

    override fun getSongList(): List<String> {
        return songs
    }

    override fun getCurrentSongIndex(): Int {
        return currentSongIndex
    }

    override fun getFavoriteSongs(): List<String> {
        return favoriteSongs.toList()
    }

    override fun toggleFavoriteSong(songIndex: Int): Boolean {
        if (songIndex !in songs.indices) {
            return false
        }

        val song = songs[songIndex]
        if (favoriteSongs.contains(song)) {
            favoriteSongs.remove(song)
            return false
        }

        favoriteSongs.add(song)
        return true
    }

    override fun isSongFavorite(songIndex: Int): Boolean {
        if (songIndex !in songs.indices) {
            return false
        }
        return favoriteSongs.contains(songs[songIndex])
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
