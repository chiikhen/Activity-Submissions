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

    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var statusTextView: TextView

    private var songUrl = ""
    private var player: ExoPlayer? = null // Made nullable for safety

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.manage_song)

        // UI Initializations
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        statusTextView = findViewById(R.id.statusText)
        val songTitleTextView: TextView = findViewById(R.id.songTitle)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        // Retrieve data from Intent
        val fullSongData = intent.getStringExtra("EXTRA_SONG_DATA") ?: ""
        val songTitle = fullSongData.substringBefore(" - ")
        songUrl = fullSongData.substringAfter(" - ")
        songTitleTextView.text = songTitle

        // Button listeners (Check for null player before acting)
        playButton.setOnClickListener { player?.play() }
        pauseButton.setOnClickListener { player?.pause() }
        stopButton.setOnClickListener {
            player?.stop()
            player?.seekTo(0)
        }
    }

    // a. Move ExoPlayer initialization to onStart()
    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    // c. Play the music when resumed
    override fun onResume() {
        super.onResume()
        player?.play()
    }

    // b. Pause the music when the activity is not in the foreground
    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    // d. Release the player when activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            val mediaItem = MediaItem.fromUri(songUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()

            // Re-attaching the listener
            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    statusTextView.text = if (isPlaying) "Status: Playing" else "Status: Paused"
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> statusTextView.text = "Status: Buffering..."
                        Player.STATE_READY -> statusTextView.text = "Status: Ready"
                        Player.STATE_IDLE -> statusTextView.text = "Status: Stopped"
                        Player.STATE_ENDED -> statusTextView.text = "Status: Finished"
                    }
                }
            })
        }
    }

    private fun releasePlayer() {
        player?.let {
            it.release()
            player = null
        }
    }
}