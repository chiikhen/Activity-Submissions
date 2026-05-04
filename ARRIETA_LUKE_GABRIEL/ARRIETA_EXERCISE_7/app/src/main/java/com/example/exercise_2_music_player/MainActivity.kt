package com.example.exercise_2_music_player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), MusicPlayerInterface {

    private val songsList = listOf(
        Song(
            "Love Me Not",
            "Ravyn Lenae",
            "https://uneven-apricot-mwxdnaxvop.edgeone.app/Ravyn%20Lenae%20-%20Love%20Me%20Not.mp3",
            R.drawable.love_me_not
        ),
        Song(
            "Angleyes",
            "ABBA",
            "https://uneven-apricot-mwxdnaxvop.edgeone.app/ABBA%20-%20Angeleyes%20(Lyrics).mp3",
            R.drawable.angeleyes
        ),
        Song(
            "Tensionado",
            "Soapdish",
            "https://uneven-apricot-mwxdnaxvop.edgeone.app/Tensionado%20(Lyrics)%20-%20Soapdish.mp3",
            R.drawable.tensionado
        ),
        Song(
            "I love you so",
            "The Walters",
            "https://uneven-apricot-mwxdnaxvop.edgeone.app/The%20Walters%20-%20I%20Love%20You%20So%20(Lyrics).mp3",
            R.drawable.i_love_you_so
        ),
        Song(
            "Ang Pag-ibig ay Kanibalismo",
            "fitterkarma",
            "https://uneven-apricot-mwxdnaxvop.edgeone.app/Pag%20ibig%20ay%20Kanibalismo%20II%20-%20fitterkarma%20(Lyrics).mp3",
            R.drawable.pag_ibig_ay_kanibalismo
        ),
        Song(
            "So Easy (To Fall In Love)",
            "Olivia Dean",
            "https://native-crimson-urnms13xjy.edgeone.app/Olivia%20Dean%20-%20So%20Easy%20(To%20Fall%20In%20Love)%20(Lyrics).mp3",
            R.drawable.so_easy
        ),
        Song(
            "Without Me",
            "Halsey",
            "https://native-crimson-urnms13xjy.edgeone.app/Halsey%20-%20Without%20Me%20(Lyrics).mp3",
            R.drawable.without_me
        ),
        Song(
            "To the Bone",
            "Pamungkas",
            "https://native-crimson-urnms13xjy.edgeone.app/Pamungkas%20-%20To%20the%20bone%20(lyrics).mp3",
            R.drawable.to_the_bone
        )
    )
    private val favoriteSongPositions = linkedSetOf<Int>()

    private var currentSongPosition = -1
    private lateinit var playerFragment: PlayerFragment
    private lateinit var player: ExoPlayer
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false
    private var wasPlaying = false
    private var playerStatusText = "Paused"
    private var playerVolumeProgress = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupWindowInsets()
        setupNavigation()
        setupPlayerFragment(savedInstanceState)
        initializePlayer()
    }

    private fun setupWindowInsets() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.main)
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            toolbar.setPadding(
                toolbar.paddingLeft,
                systemBars.top,
                toolbar.paddingRight,
                toolbar.paddingBottom
            )
            bottomNavigationView.setPadding(
                bottomNavigationView.paddingLeft,
                bottomNavigationView.paddingTop,
                bottomNavigationView.paddingRight,
                systemBars.bottom
            )
            navigationView.setPadding(
                navigationView.paddingLeft,
                systemBars.top,
                navigationView.paddingRight,
                systemBars.bottom
            )
            insets
        }
    }

    private fun setupNavigation() {
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.main)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        setSupportActionBar(toolbar)

        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.profileFragment,
                R.id.myMusicFragment,
                R.id.favoritesFragment
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navigationView.setNavigationItemSelectedListener { item ->
            val handled = navigateToTopLevelDestination(item.itemId)
            drawerLayout.closeDrawers()
            handled
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            navigateToTopLevelDestination(item.itemId)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            syncCurrentSongSelection()
            updateNavigationSelection(destination.id)
            updatePlayerChrome(destination.id)
            syncPlayerSurfaces()
        }
    }

    private fun navigateToTopLevelDestination(destinationId: Int): Boolean {
        if (navController.currentDestination?.id == destinationId) {
            return true
        }

        return runCatching {
            navController.navigate(
                destinationId,
                null,
                navOptions {
                    launchSingleTop = true
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = false
                    }
                }
            )
        }.isSuccess
    }

    private fun updateNavigationSelection(destinationId: Int) {
        if (destinationId == R.id.fullPlayerFragment) {
            return
        }

        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        navigationView.menu.findItem(R.id.myMusicFragment)?.isChecked =
            destinationId == R.id.myMusicFragment
        navigationView.menu.findItem(R.id.profileFragment)?.isChecked =
            destinationId == R.id.profileFragment

        bottomNavigationView.menu.findItem(R.id.myMusicFragment)?.isChecked =
            destinationId == R.id.myMusicFragment
        bottomNavigationView.menu.findItem(R.id.favoritesFragment)?.isChecked =
            destinationId == R.id.favoritesFragment
    }

    private fun updatePlayerChrome(destinationId: Int) {
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val playerContainer = findViewById<FrameLayout>(R.id.playerFragmentContainer)
        val isFullPlayer = destinationId == R.id.fullPlayerFragment

        toolbar.visibility = if (isFullPlayer) View.GONE else View.VISIBLE
        bottomNavigationView.visibility = if (isFullPlayer) View.GONE else View.VISIBLE
        playerContainer.visibility = if (isFullPlayer) View.GONE else View.VISIBLE
        drawerLayout.setDrawerLockMode(
            if (isFullPlayer) DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            else DrawerLayout.LOCK_MODE_UNLOCKED
        )
    }

    private fun setupPlayerFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            playerFragment = PlayerFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.playerFragmentContainer, playerFragment)
                .commit()
        } else {
            playerFragment =
                supportFragmentManager.findFragmentById(R.id.playerFragmentContainer) as? PlayerFragment
                    ?: PlayerFragment().also {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.playerFragmentContainer, it)
                            .commit()
                    }
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        player.volume = playerVolumeProgress / 100f

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    playerStatusText = "Playing"
                    startSeekBarUpdate()
                } else {
                    playerStatusText = "Paused"
                    stopSeekBarUpdate()
                }
                syncPlayerSurfaces()
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> playerStatusText = "Buffering..."
                    Player.STATE_READY -> {
                        if (!player.isPlaying) {
                            playerStatusText = "Ready"
                        }
                        if (player.currentPosition == 0L && !wasPlaying) {
                            player.play()
                            wasPlaying = true
                        }
                    }
                    Player.STATE_IDLE -> playerStatusText = "Idle"
                    Player.STATE_ENDED -> {
                        playerStatusText = "Ended"
                        stopSeekBarUpdate()
                        wasPlaying = false
                        onNextClicked()
                    }
                }
                syncPlayerSurfaces()
            }
        })
    }

    override fun onSongSelected(position: Int) {
        currentSongPosition = position
        loadSong(position)
        syncCurrentSongSelection()
        syncPlayerSurfaces()
    }

    override fun onNextClicked() {
        if (songsList.isEmpty()) return
        if (currentSongPosition < songsList.size - 1) {
            currentSongPosition++
            loadSong(currentSongPosition)
            syncCurrentSongSelection()
            syncPlayerSurfaces()
        } else {
            Toast.makeText(this, "Last song in playlist", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPreviousClicked() {
        if (songsList.isEmpty()) return
        if (currentSongPosition > 0) {
            currentSongPosition--
            loadSong(currentSongPosition)
            syncCurrentSongSelection()
            syncPlayerSurfaces()
        } else {
            Toast.makeText(this, "First song in playlist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun syncCurrentSongSelection() {
        val currentFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)
                ?.childFragmentManager
                ?.primaryNavigationFragment

        (currentFragment as? PlayableSongList)?.syncPlayingSong(currentSongPosition)
    }

    private fun loadSong(position: Int) {
        if (position !in songsList.indices) return

        val song = songsList[position]
        playerStatusText = "Loading..."

        player.apply {
            stop()
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(song.url))
            prepare()
        }

        syncPlayerSurfaces()
    }

    fun playMusic() {
        if (currentSongPosition == -1 && songsList.isNotEmpty()) {
            onSongSelected(0)
        } else {
            if (!player.isPlaying && player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            player.play()
            wasPlaying = true
            syncPlayerSurfaces()
        }
    }

    fun pauseMusic() {
        player.pause()
        wasPlaying = false
        syncPlayerSurfaces()
    }

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            if (!isUserSeeking) {
                syncPlayerSurfaces()
            }
            handler.postDelayed(this, 100)
        }
    }

    private fun startSeekBarUpdate() {
        handler.post(updateSeekBarRunnable)
    }

    private fun stopSeekBarUpdate() {
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    private fun formatTime(millis: Long): String {
        val safeMillis = millis.coerceAtLeast(0L)
        val seconds = (safeMillis / 1000).toInt()
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }

    fun getSongs(): List<Song> = songsList

    fun getCurrentSongPosition(): Int = currentSongPosition

    fun getFavoriteSongPositions(): List<Int> = favoriteSongPositions.toList()

    fun isFavorite(position: Int): Boolean = favoriteSongPositions.contains(position)

    fun isCurrentSongPlaying(): Boolean = ::player.isInitialized && player.isPlaying

    fun getPlayerUiState(): PlayerUiState {
        val song = songsList.getOrNull(currentSongPosition)
        val duration = if (::player.isInitialized && player.duration > 0) player.duration else 0L
        val currentPosition = if (::player.isInitialized) {
            player.currentPosition.coerceAtLeast(0L)
        } else {
            0L
        }
        val progress = if (duration > 0) {
            ((currentPosition * 100) / duration).toInt()
        } else {
            0
        }

        return PlayerUiState(
            song = song,
            status = playerStatusText,
            isPlaying = isCurrentSongPlaying(),
            isFavorite = currentSongPosition != -1 && isFavorite(currentSongPosition),
            currentTimeText = formatTime(currentPosition),
            totalTimeText = formatTime(duration),
            progress = progress,
            volumeProgress = playerVolumeProgress
        )
    }

    fun addFavorite(position: Int) {
        if (position in songsList.indices) {
            favoriteSongPositions.add(position)
        }
    }

    fun removeFavorite(position: Int) {
        favoriteSongPositions.remove(position)
    }

    fun showFavoriteConfirmation(position: Int) {
        if (position !in songsList.indices) return

        val song = songsList[position]
        val isCurrentlyFavorite = isFavorite(position)
        val dialogMessage = if (isCurrentlyFavorite) {
            "Remove \"${song.name}\" from your favorites?"
        } else {
            "Add \"${song.name}\" to your favorites?"
        }

        AlertDialog.Builder(this)
            .setTitle("Favorites")
            .setMessage(dialogMessage)
            .setPositiveButton("Yes") { _, _ ->
                if (isCurrentlyFavorite) {
                    removeFavorite(position)
                } else {
                    addFavorite(position)
                }
                notifyFavoriteStateChanged()
            }
            .setNegativeButton("No", null)
            .show()
    }

    fun handleCurrentSongFavoriteClick() {
        if (currentSongPosition == -1) {
            Toast.makeText(this, "Select a song first", Toast.LENGTH_SHORT).show()
            return
        }

        showFavoriteConfirmation(currentSongPosition)
    }

    private fun notifyFavoriteStateChanged() {
        syncPlayerSurfaces()

        val currentFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)
                ?.childFragmentManager
                ?.primaryNavigationFragment

        (currentFragment as? FavoriteStateAware)?.refreshFavoriteState()
    }

    fun beginSeekGesture() {
        isUserSeeking = true
    }

    fun previewSeekTime(progress: Int): String {
        val duration = if (::player.isInitialized && player.duration > 0) player.duration else 0L
        val previewPosition = if (duration > 0) {
            (duration * progress / 100).toLong()
        } else {
            0L
        }
        return formatTime(previewPosition)
    }

    fun completeSeekGesture(progress: Int) {
        val duration = if (::player.isInitialized && player.duration > 0) player.duration else 0L
        if (duration > 0) {
            val position = (duration * progress / 100).toLong()
            player.seekTo(position)
        }
        isUserSeeking = false
        syncPlayerSurfaces()
    }

    fun setPlayerVolume(progress: Int) {
        playerVolumeProgress = progress.coerceIn(0, 100)
        if (::player.isInitialized) {
            player.volume = playerVolumeProgress / 100f
        }
        syncPlayerSurfaces()
    }

    fun openFullPlayer() {
        if (navController.currentDestination?.id != R.id.fullPlayerFragment) {
            navController.navigate(R.id.fullPlayerFragment)
        }
    }

    fun closeFullPlayer() {
        navController.navigateUp()
    }

    private fun getFullPlayerFragment(): FullPlayerFragment? {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        return navHostFragment?.childFragmentManager?.primaryNavigationFragment as? FullPlayerFragment
    }

    private fun syncPlayerSurfaces() {
        val state = getPlayerUiState()
        val surfaces = mutableListOf<PlayerSurface>()

        if (::playerFragment.isInitialized) {
            surfaces.add(playerFragment)
        }
        getFullPlayerFragment()?.let(surfaces::add)

        surfaces.forEach { surface ->
            surface.renderPlayerState(state)
        }
    }

    override fun onResume() {
        super.onResume()
        if (wasPlaying && currentSongPosition != -1) {
            player.play()
        }
        syncPlayerSurfaces()
    }

    override fun onPause() {
        super.onPause()
        if (player.isPlaying) {
            wasPlaying = player.isPlaying
            player.pause()
        }
        stopSeekBarUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        stopSeekBarUpdate()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
