
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
    private lateinit var songTitleTextView: TextView
    private lateinit var songStatusTextView: TextView

    private var songUrl = ""
    private var songTitle = ""

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

        // Retrieve the song URL from the intent
        val songData = intent.getStringExtra("SONG_DATA") ?: ""
        songTitle = songData.substringBefore(" - ")
        songUrl = songData.substringAfter(" - ")

        // Setup the button functions
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)

        // Setup the Song title
        songTitleTextView = findViewById(R.id.songTitleTextView)
        songStatusTextView = findViewById(R.id.songStatusTextView)
        songTitleTextView.text = songTitle

        // Setup the button listeners
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
            player.seekTo(0)
        }
    }

    override fun onStart() {
        super.onStart()

        // Setup the ExoPlayer
        player = ExoPlayer.Builder(this).build()

        // Setup the media item to play using the URL retrieved from the intent
        val mediaItem = MediaItem.fromUri(songUrl)
        player.setMediaItem(mediaItem)
        player.prepare()

        // This listener will update the song status based on the player's state
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songStatusTextView.text = "Playing"
                } else {
                    // Only update to "Paused" if the player is not in IDLE state
                    if (player.playbackState != Player.STATE_IDLE) {
                        songStatusTextView.text = "Paused"
                    }
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> {
                        songStatusTextView.text = "Buffering..."
                    }
                    Player.STATE_READY -> {
                        songStatusTextView.text = "Ready"
                    }
                    Player.STATE_IDLE -> {
                        songStatusTextView.text = "Idle"
                    }
                    Player.STATE_ENDED -> {
                        songStatusTextView.text = "Ended"
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        if (::player.isInitialized) {
            player.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::player.isInitialized) {
            player.release()
        }
    }
}