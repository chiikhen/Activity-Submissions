package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class backup : AppCompatActivity() {

    // UI elements
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var songTitle: TextView

    // Song data
    private var songUrl: String = ""
    private var songName: String = ""

    // ExoPlayer
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.manage_song)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // UI Initialization
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        songTitle = findViewById(R.id.songTitle)

        // ✅ Safer intent handling
        val fullSongData = intent.getStringExtra("SONG_DATA")

        if (fullSongData != null && fullSongData.contains(" - ")) {
            songName = fullSongData.substringBefore(" - ")
            songUrl = fullSongData.substringAfter(" - ")
        } else {
            songName = "Unknown Song"
            songUrl = ""
        }

        songTitle.text = songName

        // Button listeners
        playButton.setOnClickListener {
            if (::player.isInitialized) {
                player.play()
            }
        }

        pauseButton.setOnClickListener {
            if (::player.isInitialized) {
                player.pause()
            }
        }

        stopButton.setOnClickListener {
            if (::player.isInitialized) {
                player.stop()
                player.seekTo(0)
            }
        }
    }

    // Initialize ExoPlayer in onStart()
    override fun onStart() {
        super.onStart()

        if (songUrl.isNotEmpty()) {

            player = ExoPlayer.Builder(this).build()

            val mediaItem = MediaItem.fromUri(songUrl)
            player.setMediaItem(mediaItem)
            player.prepare()

            player.addListener(object : Player.Listener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        songTitle.text = "Playing: $songName"
                    } else {
                        songTitle.text = "Paused: $songName"
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> songTitle.text = "Buffering..."
                        Player.STATE_READY -> songTitle.text = "Ready"
                        Player.STATE_IDLE -> songTitle.text = "Idle"
                        Player.STATE_ENDED -> songTitle.text = "Playback Ended"
                    }
                }
            })
        }
    }

    // Pause music when activity is not visible
    override fun onPause() {
        super.onPause()
        if (::player.isInitialized) {
            player.pause()
        }
    }

    // Resume music safely
    override fun onResume() {
        super.onResume()
        if (::player.isInitialized && !player.isPlaying) {
            player.play()
        }
    }

    // Release player to prevent memory leaks
    override fun onDestroy() {
        super.onDestroy()
        if (::player.isInitialized) {
            player.release()
        }
    }
}