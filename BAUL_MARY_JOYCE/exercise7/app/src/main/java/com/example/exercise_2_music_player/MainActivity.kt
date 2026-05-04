package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), MusicNavigationListener {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    private var currentIndex = 0
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private lateinit var player: ExoPlayer
    private var currentUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        player = ExoPlayer.Builder(this).build()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.main)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.music_list_dest, R.id.favorites_dest, R.id.profile_dest),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNav.setupWithNavController(navController)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.playerContainer, ManageSong(), "PLAYER_TAG")
                .commit()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun getPlayerInstance(): ExoPlayer = player

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun onMusicSelected(index: Int) {
        currentIndex = index
        updatePlayer()
    }

    override fun onNextPressed() {
        currentIndex = (currentIndex + 1) % songs.size
        updatePlayer()
    }

    override fun onPreviousPressed() {
        currentIndex = if (currentIndex > 0) currentIndex - 1 else songs.size - 1
        updatePlayer()
    }

    private fun updatePlayer() {
        val parts = songs[currentIndex].split(" - ")
        val title = parts[0]
        val url = parts[1]

        if (currentUrl != url) {
            currentUrl = url
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
        }

        if (!player.isPlaying) {
            player.play()
        }

        val fragment = supportFragmentManager
            .findFragmentByTag("PLAYER_TAG") as? ManageSong

        fragment?.updateUI(title)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}