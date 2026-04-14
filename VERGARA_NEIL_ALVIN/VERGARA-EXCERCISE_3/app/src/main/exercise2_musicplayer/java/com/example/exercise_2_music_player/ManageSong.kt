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
    private lateinit var songTitle: TextView
    private lateinit var statusTextView: TextView

    // URL retrieve from the intent
    private var songUrl = ""

    // setup the exoplayer
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

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        songTitle = findViewById<TextView>(R.id.songTitle)
        statusTextView = findViewById<TextView>(R.id.statusTextView)

        // Retrieve the song URL from the intent
        val songData = intent.getStringExtra("EXTRA_SONG_DATA")

        if (songData != null && songData.contains(" - ")) {
            val parts = songData.split(" - ")
            val title = parts[0]
            songUrl = parts[1]
            // Setup the Song title using substringBefore
            songTitle.text = songData.substringBefore(" - ")
        } else {
            songTitle.text = "Unknown Song"
            songUrl = ""
        }

        // Setup the button functions
        setupButtonListeners()
    }

    override fun onStart() {
        super.onStart()
        // Move the ExoPlayer initialization from onCreate() to onStart()
        player = ExoPlayer.Builder(this).build()

        // Put the song URL to the media item
        if (songUrl.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(songUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
        }

        // Complete the addListener function to update status based on player state
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    statusTextView.text = "Now Playing..."
                } else {
                    statusTextView.text = "Paused"
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> {
                        statusTextView.text = "Buffering..."
                    }
                    Player.STATE_READY -> {
                        statusTextView.text = "Ready to Play"
                    }
                    Player.STATE_IDLE -> {
                        statusTextView.text = "Idle"
                    }
                    Player.STATE_ENDED -> {
                        statusTextView.text = "Playback Ended"
                    }
                }
            }
        })
    }

    private fun setupButtonListeners() {
        playButton.setOnClickListener {
            if (!player.isPlaying && player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            player.play()
        }

        pauseButton.setOnClickListener {
            player.pause()
        }

        stopButton.setOnClickListener {
            player.stop()
            // Reset the music
            player.seekTo(0)
        }
    }

    override fun onPause() {
        super.onPause()
        // Pause the music
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        // Play the music
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Call the release() method of the player
        player.release()
    }
}