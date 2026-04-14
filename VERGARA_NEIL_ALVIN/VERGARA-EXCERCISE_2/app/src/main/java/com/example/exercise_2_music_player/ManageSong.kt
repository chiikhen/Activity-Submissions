package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class ManageSong : AppCompatActivity() {
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var songTitle: TextView
    private var songUrl = ""
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.manage_song)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        // Initialize UI elements
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        songTitle = findViewById(R.id.songTitle)

        // Get song data from intent
        val songData = intent.getStringExtra("EXTRA_SONG_DATA")
        if (songData != null && songData.contains(" - ")) {
            songTitle.text = songData.substringBefore(" - ")
            songUrl = songData.substringAfter(" - ")
        } else {
            songTitle.text = "Unknown Song"
        }

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()

        if (songUrl.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(songUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
        }

        // Setup button listeners
        playButton.setOnClickListener {
            if (!player.isPlaying) {
                player.play()
            }
        }

        pauseButton.setOnClickListener {
            player.pause()
        }

        stopButton.setOnClickListener {
            player.stop()
            player.seekTo(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}