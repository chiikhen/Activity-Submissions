package com.example.tabares_exercise_2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), OnSongInteractionListener {

    companion object {
        private const val FAVORITES_PREFS = "player_favorites"
        private const val KEY_FAVORITE_URLS = "favorite_urls"
        private const val PLAYBACK_PREFS = "player_playback"
        private const val KEY_LAST_SONG_URL = "last_song_url"
        private const val KEY_LAST_SONG_POSITION = "last_song_position"
        private const val KEY_LAST_WAS_PLAYING = "last_was_playing"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var topAppBar: Toolbar

    private var player: ExoPlayer? = null
    private var currentSong: Song? = null
    private var currentSongIndex = -1
    private var pendingRestorePosition = 0L
    private var shouldResumeOnLaunch = false
    private val currentSongList: List<Song> = SongListFragment.SONGS
    private val favoriteSongUrls = linkedSetOf<String>()

    private lateinit var miniPlayerContainer: android.view.View
    private lateinit var miniAlbumArt: ImageView
    private lateinit var miniSongTitle: TextView
    private lateinit var miniSongArtist: TextView
    private lateinit var miniPlayPauseButton: TextView

    private val uiHandler = Handler(Looper.getMainLooper())
    private val miniUpdateRunnable = object : Runnable {
        override fun run() {
            updateMiniPlayer()
            uiHandler.postDelayed(this, 250)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        loadFavoriteSongs()
        loadPlaybackState()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        topAppBar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navView)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        miniPlayerContainer = findViewById(R.id.miniPlayerContainer)
        miniAlbumArt = findViewById(R.id.miniAlbumArt)
        miniSongTitle = findViewById(R.id.miniSongTitle)
        miniSongArtist = findViewById(R.id.miniSongArtist)
        miniPlayPauseButton = findViewById(R.id.miniPlayPauseButton)

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

        fun navigateIfNeeded(destinationId: Int): Boolean {
            if (navController.currentDestination?.id == destinationId) return true
            navController.navigate(destinationId)
            return true
        }

        fun updateDrawerSelection(checkedItemId: Int?) {
            val menu = navigationView.menu
            for (index in 0 until menu.size()) {
                val item = menu.getItem(index)
                item.isChecked = item.itemId == checkedItemId
            }
        }

        fun updateBottomNavSelection(checkedItemId: Int?) {
            val menu = bottomNavigationView.menu
            for (index in 0 until menu.size()) {
                val item = menu.getItem(index)
                item.isChecked = item.itemId == checkedItemId
            }
        }

        navigationView.setNavigationItemSelectedListener { item ->
            val handled = when (item.itemId) {
                R.id.drawerNowPlaying -> navigateIfNeeded(R.id.myMusicFragment)
                R.id.profileFragment -> navigateIfNeeded(item.itemId)
                else -> false
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            handled
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.myMusicFragment,
                R.id.favoritesFragment -> navigateIfNeeded(item.itemId)
                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nowPlayingFragment -> {
                    updateDrawerSelection(null)
                    updateBottomNavSelection(null)
                }
                R.id.profileFragment -> {
                    updateDrawerSelection(R.id.profileFragment)
                    updateBottomNavSelection(null)
                }
                R.id.myMusicFragment -> {
                    updateDrawerSelection(R.id.drawerNowPlaying)
                    updateBottomNavSelection(R.id.myMusicFragment)
                }
                R.id.favoritesFragment -> {
                    updateDrawerSelection(null)
                    updateBottomNavSelection(R.id.favoritesFragment)
                }
            }

            bottomNavigationView.visibility =
                if (destination.id == R.id.nowPlayingFragment) android.view.View.GONE
                else android.view.View.VISIBLE

            topAppBar.visibility =
                if (destination.id == R.id.nowPlayingFragment) android.view.View.GONE
                else android.view.View.VISIBLE

            updateMiniPlayer()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        miniPlayerContainer.setOnClickListener {
            if (currentSong != null && navController.currentDestination?.id != R.id.nowPlayingFragment) {
                navController.navigate(R.id.nowPlayingFragment)
            }
        }

        miniPlayPauseButton.setOnClickListener {
            onTogglePlayPause()
        }

        updateMiniPlayer()
    }

    override fun onStart() {
        super.onStart()
        restorePlaybackState()
        uiHandler.post(miniUpdateRunnable)
    }

    override fun onResume() {
        super.onResume()
        window.setBackgroundDrawableResource(R.drawable.bg_main_gradient)
        window.attributes = window.attributes.apply { alpha = 1f }
        window.decorView.alpha = 1f
        window.decorView.visibility = android.view.View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        uiHandler.removeCallbacks(miniUpdateRunnable)
        savePlaybackState(player?.isPlaying == true)
        player?.pause()
        updateMiniPlayer()
    }

    override fun onDestroy() {
        if (isFinishing && !isChangingConfigurations) {
            clearPlaybackState()
        }
        super.onDestroy()
        player?.release()
        player = null
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getNavController() =
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

    private fun ensurePlayer(): ExoPlayer {
        val existing = player
        if (existing != null) return existing

        val created = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        created.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updateMiniPlayer()
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    onNextSong()
                }
                updateMiniPlayer()
            }
        })

        player = created
        return created
    }

    private fun playSong(
        song: Song,
        index: Int,
        startPositionMs: Long = 0L,
        autoPlay: Boolean = true
    ) {
        val p = ensurePlayer()
        val activeUrl = p.currentMediaItem?.localConfiguration?.uri?.toString()

        currentSong = song
        currentSongIndex = index

        if (activeUrl != song.url) {
            p.setMediaItem(MediaItem.fromUri(song.url))
            p.prepare()
        }

        if (startPositionMs > 0L) {
            p.seekTo(startPositionMs)
        } else if (activeUrl != song.url) {
            p.seekTo(0L)
        }

        if (autoPlay) {
            p.play()
        } else {
            p.pause()
        }

        pendingRestorePosition = startPositionMs.coerceAtLeast(0L)
        shouldResumeOnLaunch = autoPlay
        savePlaybackState(autoPlay)
        updateMiniPlayer()
    }

    private fun updateMiniPlayer() {
        val song = currentSong
        val p = player
        val isOnNowPlaying = getNavController().currentDestination?.id == R.id.nowPlayingFragment

        if (song == null || isOnNowPlaying) {
            miniPlayerContainer.visibility = android.view.View.GONE
            return
        }

        miniPlayerContainer.visibility = android.view.View.VISIBLE
        miniAlbumArt.setImageResource(song.imageRes)
        miniSongTitle.text = song.name
        miniSongArtist.text = song.artist

        val playing = p?.isPlaying == true
        miniPlayPauseButton.text = if (playing) "||" else "\u25B6"
    }

    private fun loadFavoriteSongs() {
        favoriteSongUrls.clear()
        val savedFavorites = getSharedPreferences(FAVORITES_PREFS, MODE_PRIVATE)
            .getStringSet(KEY_FAVORITE_URLS, emptySet())
            .orEmpty()
        favoriteSongUrls.addAll(savedFavorites)
    }

    private fun saveFavoriteSongs() {
        getSharedPreferences(FAVORITES_PREFS, MODE_PRIVATE)
            .edit()
            .putStringSet(KEY_FAVORITE_URLS, favoriteSongUrls.toSet())
            .apply()
    }

    private fun loadPlaybackState() {
        val prefs = getSharedPreferences(PLAYBACK_PREFS, MODE_PRIVATE)
        val savedUrl = prefs.getString(KEY_LAST_SONG_URL, null) ?: return
        val savedIndex = currentSongList.indexOfFirst { it.url == savedUrl }

        if (savedIndex >= 0) {
            currentSongIndex = savedIndex
            currentSong = currentSongList[savedIndex]
            pendingRestorePosition = prefs.getLong(KEY_LAST_SONG_POSITION, 0L).coerceAtLeast(0L)
            shouldResumeOnLaunch = prefs.getBoolean(KEY_LAST_WAS_PLAYING, false)
        }
    }

    private fun savePlaybackState(wasPlaying: Boolean) {
        val prefs = getSharedPreferences(PLAYBACK_PREFS, MODE_PRIVATE).edit()
        val song = currentSong

        if (song == null) {
            clearPlaybackState()
            return
        }

        val currentPosition = player?.currentPosition?.takeIf { it > 0L } ?: pendingRestorePosition
        pendingRestorePosition = currentPosition.coerceAtLeast(0L)
        shouldResumeOnLaunch = wasPlaying

        prefs.putString(KEY_LAST_SONG_URL, song.url)
            .putLong(KEY_LAST_SONG_POSITION, pendingRestorePosition)
            .putBoolean(KEY_LAST_WAS_PLAYING, wasPlaying)
            .apply()
    }

    private fun clearPlaybackState() {
        getSharedPreferences(PLAYBACK_PREFS, MODE_PRIVATE)
            .edit()
            .remove(KEY_LAST_SONG_URL)
            .remove(KEY_LAST_SONG_POSITION)
            .remove(KEY_LAST_WAS_PLAYING)
            .apply()

        currentSong = null
        currentSongIndex = -1
        pendingRestorePosition = 0L
        shouldResumeOnLaunch = false
    }

    private fun restorePlaybackState() {
        val song = currentSong ?: return
        val index = currentSongIndex.takeIf { it in currentSongList.indices }
            ?: currentSongList.indexOfFirst { it.url == song.url }

        if (index < 0) return

        playSong(
            song = song,
            index = index,
            startPositionMs = pendingRestorePosition,
            autoPlay = shouldResumeOnLaunch
        )
    }

    override fun onSongSelected(song: Song, position: Int) {
        playSong(song, position)

        val navController = getNavController()
        if (navController.currentDestination?.id != R.id.nowPlayingFragment) {
            navController.navigate(R.id.nowPlayingFragment)
        }
    }

    override fun onNextSong() {
        if (currentSongList.isEmpty()) return

        val nextIndex = if (currentSongIndex < 0) 0 else (currentSongIndex + 1) % currentSongList.size
        playSong(currentSongList[nextIndex], nextIndex)
    }

    override fun onPreviousSong() {
        if (currentSongList.isEmpty()) return

        val prevIndex = if (currentSongIndex <= 0) currentSongList.size - 1 else currentSongIndex - 1
        playSong(currentSongList[prevIndex], prevIndex)
    }

    override fun onTogglePlayPause() {
        val p = player ?: run {
            val song = currentSong ?: return
            val index = currentSongIndex.takeIf { it in currentSongList.indices }
                ?: currentSongList.indexOfFirst { it.url == song.url }
            if (index < 0) return
            playSong(song, index, pendingRestorePosition, true)
            return
        }
        if (p.isPlaying) {
            p.pause()
        } else {
            p.play()
        }
        savePlaybackState(p.isPlaying)
        updateMiniPlayer()
    }

    override fun onToggleFavorite(song: Song): Boolean {
        val isFavoriteNow = if (favoriteSongUrls.contains(song.url)) {
            favoriteSongUrls.remove(song.url)
            false
        } else {
            favoriteSongUrls.add(song.url)
            true
        }

        saveFavoriteSongs()
        updateMiniPlayer()
        return isFavoriteNow
    }

    override fun onSeekTo(progress: Int) {
        val p = player ?: return
        if (p.duration <= 0) return

        val clamped = progress.coerceIn(0, 100)
        val target = (p.duration * clamped) / 100
        p.seekTo(target)
        pendingRestorePosition = target
        savePlaybackState(p.isPlaying)
    }

    override fun onMinimizeNowPlaying() {
        val navController = getNavController()
        if (navController.currentDestination?.id == R.id.nowPlayingFragment) {
            navController.navigate(R.id.myMusicFragment)
        }
    }

    override fun isFavorite(song: Song): Boolean = favoriteSongUrls.contains(song.url)

    override fun getFavoriteSongs(): List<Song> =
        currentSongList.filter { favoriteSongUrls.contains(it.url) }

    override fun getPlaybackSnapshot(): PlaybackSnapshot {
        val p = player
        return PlaybackSnapshot(
            song = currentSong,
            isPlaying = p?.isPlaying == true,
            currentPosition = p?.currentPosition ?: 0L,
            duration = p?.duration?.takeIf { it > 0 } ?: 0L
        )
    }
}
