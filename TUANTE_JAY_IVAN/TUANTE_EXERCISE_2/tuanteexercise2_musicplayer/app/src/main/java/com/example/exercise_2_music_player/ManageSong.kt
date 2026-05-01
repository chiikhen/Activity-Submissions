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

class ManageSong : AppCompatActivity() {

    // UI elements
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var statusText: TextView

    // Song info from Intent
    private var songUrl = ""
    private var songFull = ""

    // ExoPlayer
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.manage_song)

        // Handle system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve song info from Intent
        songUrl = intent.getStringExtra("SONG_URL") ?: ""
        songFull = intent.getStringExtra("SONG_FULL") ?: ""

        // Initialize UI elements
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        statusText = findViewById(R.id.songTitle)

        statusText.text = "Selected: ${songFull.substringBefore(" - ")}"

        setupButtonListeners()
    }

    // Setup ExoPlayer
    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        val mediaItem = MediaItem.fromUri(songUrl)
        player.setMediaItem(mediaItem)

        // Listener to update statusText
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                statusText.text = if (isPlaying)
                    "Playing: ${songFull.substringBefore(" - ")}"
                else
                    "Paused: ${songFull.substringBefore(" - ")}"
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> statusText.text = "Buffering..."
                    Player.STATE_READY -> {
                        if (player.isPlaying)
                            statusText.text = "Playing: ${songFull.substringBefore(" - ")}"
                        else
                            statusText.text = "Paused: ${songFull.substringBefore(" - ")}"
                    }
                    Player.STATE_IDLE -> statusText.text = "Idle"
                    Player.STATE_ENDED -> statusText.text = "Song Ended"
                }
            }
        })

        // Prepare asynchronously (won’t block UI)
        player.prepare()
    }

    // Setup buttons
    private fun setupButtonListeners() {
        playButton.setOnClickListener {
            if (player.playbackState == Player.STATE_READY) {
                player.play()
            }
        }
        pauseButton.setOnClickListener { player.pause() }
        stopButton.setOnClickListener {
            player.stop()
            player.seekTo(0)
            statusText.text = "Stopped"
        }
    }

    // Lifecycle
    override fun onStart() {
        super.onStart()
        setupPlayer() // Initialize ExoPlayer per instructions
    }

    override fun onResume() {
        super.onResume()
        // Only play if player is ready
        if (player.playbackState == Player.STATE_READY || player.playbackState == Player.STATE_BUFFERING) {
            player.play()
        }
    }

    override fun onPause() {
        super.onPause()
        player.pause() // Pause music when activity pauses
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release() // Release player
    }
}
