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
    private lateinit var statusTextView: TextView

    private var songUrl = ""
    private var songName = ""
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fragment_player)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the song URL from the intent [cite: 9]
        val songData = intent.getStringExtra("SONG_DATA") ?: ""

        // Requirement: Setup the Song title using substringBefore
        songName = songData.substringBefore(" - ")
        songUrl = songData.substringAfter(" - ")

        // Requirement: Setup the button functions
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)

        // Link the TextView from XML to our variable [cite: 13]
        statusTextView = findViewById(R.id.songTitle)

        setupButtonListeners()
    }

    // Requirement: Move ExoPlayer initialization to onStart() [cite: 15, 16]
    override fun onStart() {
        super.onStart()
        player = ExoPlayer.Builder(this).build()

        // Requirement: Put the song URL to the media Item [cite: 12]
        val mediaItem = MediaItem.fromUri(songUrl)
        player.setMediaItem(mediaItem)
        player.prepare()

        setupPlayerListener()
    }

    // Requirement: Play the music on Resume [cite: 19, 20]
    override fun onResume() {
        super.onResume()
        player.play()
    }

    // Requirement: Pause the music on Pause [cite: 17, 18]
    override fun onPause() {
        super.onPause()
        player.pause()
    }

    // Requirement: Call the release() method on Destroy [cite: 21, 22]
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun setupButtonListeners() {
        playButton.setOnClickListener {
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
            player.seekTo(0)
        }
    }

    // Requirement: Update text view to display status based on player state [cite: 13]
    // Requirement: Update text view to display status based on player state
    // Requirement: Update text view to display status based on player state
    private fun setupPlayerListener() {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                statusTextView.text = if (isPlaying) "Playing: $songName" else "Paused: $songName"
            }

            override fun onPlaybackStateChanged(state: Int) {
                // Fix: Pass 'state' into the when block
                when (state) {
                    // Fix: Add the missing STATE_READY case to handle text updates
                    Player.STATE_READY -> {
                        statusTextView.text =
                            if (player.isPlaying) "Playing: $songName" else "Paused: $songName"
                    }

                    Player.STATE_BUFFERING -> statusTextView.text = "Buffering: $songName"
                    Player.STATE_ENDED -> statusTextView.text = "Finished: $songName"
                    Player.STATE_IDLE -> statusTextView.text = "Stopped"
                }
            }
        })
    }
}