package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(),
    MusicListFragment.OnSongSelectedListener,
    MusicPlayerFragment.OnNavigationListener {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "Song 4 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        "Song 5 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
    )

    private var currentIndex = 0
    private var isPlayerVisible = false
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var musicPlayerContainer: FrameLayout
    private lateinit var miniPlayerBar: LinearLayout
    private lateinit var miniSongTitle: TextView
    private lateinit var miniPlayPauseButton: Button
    private lateinit var miniExpandButton: Button
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Bottom Navigation
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Navigation Drawer menu
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_now_playing -> {
                    bottomNavigation.visibility = View.VISIBLE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MusicListFragment())
                        .commit()
                }
                R.id.nav_profile -> {
                    bottomNavigation.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ProfileFragment())
                        .commit()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Bottom Navigation listener
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            bottomNavigation.visibility = View.VISIBLE
            when (menuItem.itemId) {
                R.id.nav_my_music -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, MusicListFragment())
                        .commit()
                }
                R.id.nav_favorites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, FavoritesFragment())
                        .commit()
                }
            }
            true
        }

        // Mini player bar
        musicPlayerContainer = findViewById(R.id.musicPlayerContainer)
        miniPlayerBar = findViewById(R.id.miniPlayerBar)
        miniSongTitle = findViewById(R.id.miniSongTitle)
        miniPlayPauseButton = findViewById(R.id.miniPlayPauseButton)
        miniExpandButton = findViewById(R.id.miniExpandButton)

        miniExpandButton.setOnClickListener { showPlayer() }

        miniPlayPauseButton.setOnClickListener {
            getPlayerFragment()?.togglePlayPause()
        }

        // Default fragments
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MusicListFragment())
                .replace(R.id.musicPlayerContainer, MusicPlayerFragment())
                .commit()
        }

        // Back press
        onBackPressedDispatcher.addCallback(this) {
            when {
                drawerLayout.isDrawerOpen(GravityCompat.START) ->
                    drawerLayout.closeDrawer(GravityCompat.START)
                isPlayerVisible -> hidePlayer()
                else -> finish()
            }
        }
    }

    private fun showPlayer() {
        isPlayerVisible = true
        musicPlayerContainer.animate()
            .translationY(0f)
            .setDuration(300)
            .start()
    }

    fun hidePlayer() {
        isPlayerVisible = false
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()
        musicPlayerContainer.animate()
            .translationY(screenHeight)
            .setDuration(300)
            .start()
    }

    fun updateMiniBar(songName: String, isPlaying: Boolean) {
        miniPlayerBar.visibility = View.VISIBLE
        miniSongTitle.text = songName
        miniPlayPauseButton.text = if (isPlaying) "⏸" else "▶"
    }

    override fun onSongSelected(songData: String, position: Int) {
        currentIndex = position
        val songName = songData.substringBefore(" - ")
        getPlayerFragment()?.loadSong(songData)
        updateMiniBar(songName, true)
        showPlayer()
    }

    override fun onPreviousSong() {
        currentIndex = (currentIndex - 1 + songs.size) % songs.size
        getPlayerFragment()?.loadSong(songs[currentIndex])
        updateMiniBar(songs[currentIndex].substringBefore(" - "), true)
    }

    override fun onNextSong() {
        currentIndex = (currentIndex + 1) % songs.size
        getPlayerFragment()?.loadSong(songs[currentIndex])
        updateMiniBar(songs[currentIndex].substringBefore(" - "), true)
    }

    private fun getPlayerFragment(): MusicPlayerFragment? {
        return supportFragmentManager.findFragmentById(R.id.musicPlayerContainer) as? MusicPlayerFragment
    }
}