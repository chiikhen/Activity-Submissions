package com.example.exercise_2_music_player

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class ManageSong : AppCompatActivity() {

    // UI elements
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var songTitleTextView: TextView
    private lateinit var statusTextView: TextView // Added to display status

    // URL retrieve from the intent
    private var songUrl = ""

    // Setup the exoplayer
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

        // Initialize Views (Make sure these IDs match your XML)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        songTitleTextView = findViewById(R.id.songTitleTextView) // Ensure you have this ID in XML
        statusTextView = findViewById(R.id.statusTextView)       // Ensure you have this ID in XML

        // a. Retrieve the song URL/String from the intent
        val songData = intent.getStringExtra("SONG_DATA") ?: ""

        // c. Setup the Song title (use the method substringBefore to get the name)
        // Format assumed: "Song Name - https://..."
        val songName = songData.substringBefore(" - ")
        songUrl = songData.substringAfter(" - ")

        songTitleTextView.text = songName

        // b. Setup the button functions
        playButton.setOnClickListener {
            // Check if player is initialized and idle, then prepare
            if (player.playbackState == Player.STATE_IDLE) {
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

    // 3. Apply on the ManageSong activity’s life cycle

    // a. onStart() -> Move ExoPlayer initialization here
    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    // b. onPause() -> Pause the Music
    override fun onPause() {
        super.onPause()
        if (this::player.isInitialized) {
            player.pause()
        }
    }

    // c. onResume() -> Play the music
    override fun onResume() {
        super.onResume()
        if (this::player.isInitialized) {
            player.play()
        }
    }

    // d. onDestroy() -> Release the player
    override fun onDestroy() {
        super.onDestroy()
        if (this::player.isInitialized) {
            player.release()
        }
    }

    private fun initializePlayer() {
        // Setup the ExoPlayer
        player = ExoPlayer.Builder(this).build()

        // d. Put the song URL to the media Item
        val mediaItem = MediaItem.fromUri(songUrl)
        player.setMediaItem(mediaItem)
        player.prepare()

        // e. Complete the addListener function to update text view based on state
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    statusTextView.text = "Status: Playing"
                } else {
                    statusTextView.text = "Status: Paused"
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> {
                        statusTextView.text = "Status: Buffering..."
                    }
                    Player.STATE_READY -> {
                        // Only update if not currently playing (to avoid overwriting "Playing")
                        if (!player.isPlaying) {
                            statusTextView.text = "Status: Ready"
                        }
                    }
                    Player.STATE_IDLE -> {
                        statusTextView.text = "Status: Idle"
                    }
                    Player.STATE_ENDED -> {
                        statusTextView.text = "Status: Ended"
                    }
                }
            }
        })
    }
}