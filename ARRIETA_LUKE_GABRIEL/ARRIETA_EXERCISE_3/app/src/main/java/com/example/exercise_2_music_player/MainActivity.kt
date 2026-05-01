package com.example.exercise_2_music_player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MainActivity : AppCompatActivity(), MusicPlayerInterface {

    // List of songs
    private val songsList = listOf(
        Song("Love Me Not", "Ravyn Lenae", "https://uneven-apricot-mwxdnaxvop.edgeone.app/Ravyn%20Lenae%20-%20Love%20Me%20Not.mp3", R.drawable.love_me_not),
        Song("Angleyes", "ABBA", "https://uneven-apricot-mwxdnaxvop.edgeone.app/ABBA%20-%20Angeleyes%20(Lyrics).mp3", R.drawable.angeleyes),
        Song("Tensionado", "Soapdish", "https://uneven-apricot-mwxdnaxvop.edgeone.app/Tensionado%20(Lyrics)%20-%20Soapdish.mp3", R.drawable.tensionado),
        Song("I love you so", "The Walters", "https://uneven-apricot-mwxdnaxvop.edgeone.app/The%20Walters%20-%20I%20Love%20You%20So%20(Lyrics).mp3", R.drawable.i_love_you_so),
        Song("Ang Pag-ibig ay Kanibalismo", "fitterkarma", "https://uneven-apricot-mwxdnaxvop.edgeone.app/Pag%20ibig%20ay%20Kanibalismo%20II%20-%20fitterkarma%20(Lyrics).mp3", R.drawable.pag_ibig_ay_kanibalismo)
    )

    private var currentSongPosition = -1
    private lateinit var playerFragment: PlayerFragment
    private lateinit var listFragment: ListFragment
    private lateinit var player: ExoPlayer
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false
    private var wasPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            listFragment = ListFragment()
            playerFragment = PlayerFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.listFragmentContainer, listFragment)
                .replace(R.id.playerFragmentContainer, playerFragment)
                .commit()
        } else {
            // Restore fragment references after configuration change
            playerFragment = supportFragmentManager.findFragmentById(R.id.playerFragmentContainer) as? PlayerFragment
                ?: PlayerFragment()
            listFragment = supportFragmentManager.findFragmentById(R.id.listFragmentContainer) as? ListFragment
                ?: ListFragment()
        }

        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    playerFragment.updateStatus("Playing")
                    startSeekBarUpdate()
                } else {
                    playerFragment.updateStatus("Paused")
                    stopSeekBarUpdate()
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> playerFragment.updateStatus("Buffering...")
                    Player.STATE_READY -> {
                        playerFragment.updateStatus("Ready")
                        playerFragment.getTotalTimeTextView().text = formatTime(player.duration)

                        // Auto-start playback when song is ready (first time loading)
                        if (player.currentPosition == 0L && !wasPlaying) {
                            player.play()
                            wasPlaying = true
                        }
                    }
                    Player.STATE_IDLE -> playerFragment.updateStatus("Idle")
                    Player.STATE_ENDED -> {
                        playerFragment.updateStatus("Ended")
                        stopSeekBarUpdate()
                        wasPlaying = false
                        // Auto play next song
                        onNextClicked()
                    }
                }
            }
        })
    }

    override fun onSongSelected(position: Int) {
        currentSongPosition = position
        loadSong(position)
        // Highlight the playing song in the list
        listFragment.setPlayingPosition(position)
    }

    override fun onNextClicked() {
        if (songsList.isEmpty()) return
        if (currentSongPosition < songsList.size - 1) {
            currentSongPosition++
            loadSong(currentSongPosition)
            listFragment.setPlayingPosition(currentSongPosition)
        } else {
            Toast.makeText(this, "Last song in playlist", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPreviousClicked() {
        if (songsList.isEmpty()) return
        if (currentSongPosition > 0) {
            currentSongPosition--
            loadSong(currentSongPosition)
            listFragment.setPlayingPosition(currentSongPosition)
        } else {
            Toast.makeText(this, "First song in playlist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSong(position: Int) {
        if (position < 0 || position >= songsList.size) return

        val song = songsList[position]

        player.apply {
            stop()
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(song.url))
            prepare()
        }

        playerFragment.updateSongInfo(song)
        playerFragment.setAlbumArt(song.albumArtResId)
        playerFragment.updateStatus("Loading...")
        setupSeekBarListener()
    }

    private fun setupSeekBarListener() {
        val seekBar = playerFragment.getSeekBar()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = player.duration
                    if (duration > 0) {
                        val position = (duration * progress / 100).toLong()
                        playerFragment.getCurrentTimeTextView().text = formatTime(position)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val duration = player.duration
                if (duration > 0) {
                    val position = (duration * (seekBar?.progress ?: 0) / 100).toLong()
                    player.seekTo(position)
                }
                isUserSeeking = false
            }
        })
    }

    fun playMusic() {
        if (currentSongPosition == -1 && songsList.isNotEmpty()) {
            // No song selected, load first song
            onSongSelected(0)
        } else {
            if (!player.isPlaying && player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            player.play()
            wasPlaying = true
            playerFragment.updatePlayPauseButton(true)
        }
    }

    fun pauseMusic() {
        player.pause()
        wasPlaying = false
        playerFragment.updatePlayPauseButton(false)
    }

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            if (!isUserSeeking) {
                val duration = player.duration
                val currentPosition = player.currentPosition

                if (duration > 0) {
                    val progress = ((currentPosition * 100) / duration).toInt()
                    playerFragment.getSeekBar().progress = progress
                    playerFragment.getCurrentTimeTextView().text = formatTime(currentPosition)
                }
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
        val seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }


    fun getSongs(): List<Song> = songsList

    override fun onResume() {
        super.onResume()
        if (wasPlaying && currentSongPosition != -1) {
            player.play()
        }
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
}
