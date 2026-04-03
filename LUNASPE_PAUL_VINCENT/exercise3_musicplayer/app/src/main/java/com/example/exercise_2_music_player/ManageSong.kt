package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class ManageSong : AppCompatActivity() {

    //    UI elements
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var songTitle: TextView

    //    URL retrieve from the intent
    private var songUrl = ""

    //    setup the exoplayer
    private lateinit var player: ExoPlayer

    // Track if the user has started playing
    private var wasPlaying = false

    // Track playback position
    private var playbackPosition: Long = 0

    companion object {
        private const val KEY_POSITION = "playback_position"
        private const val KEY_WAS_PLAYING = "was_playing"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.manage_song)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        // Restore saved state if available
        savedInstanceState?.let {
            playbackPosition = it.getLong(KEY_POSITION, 0)
            wasPlaying = it.getBoolean(KEY_WAS_PLAYING, false)
        }

//        Retrieve the song URL from the intent
        val songData = intent.getStringExtra("songUrl") ?: ""
        songUrl = songData.substringAfter(" - ")

//        Setup the button functions
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        songTitle = findViewById(R.id.songTitle)

//        Setup the Song title (use substringBefore to get the name)
        val songName = songData.substringBefore(" - ")
        songTitle.text = songName

//        Setup the button listeners
        playButton.setOnClickListener {
            if (!player.isPlaying && player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            wasPlaying = true
            player.play()
        }

        pauseButton.setOnClickListener {
            wasPlaying = false
            player.pause()
        }

        stopButton.setOnClickListener {
            wasPlaying = false
            playbackPosition = 0
            player.stop()
//            Reset the music
            player.seekTo(0)
        }
    }

    override fun onStart() {
        super.onStart()
//        Setup the ExoPlayer
        player = ExoPlayer.Builder(this).build()
//        Setup the media item to play using the URL retrieved from the intent
        val mediaItem = MediaItem.fromUri(songUrl)
        player.setMediaItem(mediaItem)
        player.prepare()

        // Restore playback position
        if (playbackPosition > 0) {
            player.seekTo(playbackPosition)
        }

//        This listener will update the song title based on the player's state
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songTitle.text = "${songTitle.text.toString().substringBefore(" - ")} - Playing"
                } else {
                    songTitle.text = "${songTitle.text.toString().substringBefore(" - ")} - Paused"
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                val songName = intent.getStringExtra("songUrl")?.substringBefore(" - ") ?: "Song"
                if (state == Player.STATE_BUFFERING) {
                    songTitle.text = "$songName - Buffering..."
                }
                if (state == Player.STATE_READY) {
                    songTitle.text = "$songName - Ready"
                }
                if (state == Player.STATE_IDLE) {
                    songTitle.text = "$songName - Stopped"
                }
                if (state == Player.STATE_ENDED) {
                    songTitle.text = "$songName - Ended"
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
//        Save current position before pausing
        if (::player.isInitialized) {
            playbackPosition = player.currentPosition
            wasPlaying = player.isPlaying
//        Pause the Music
            player.pause()
        }
    }

    override fun onResume() {
        super.onResume()
//        Only resume if it was playing before
        if (::player.isInitialized && wasPlaying) {
            player.play()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save playback position and state
        if (::player.isInitialized) {
            outState.putLong(KEY_POSITION, player.currentPosition)
            outState.putBoolean(KEY_WAS_PLAYING, wasPlaying)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        Call the release() method of the player
        if (::player.isInitialized) {
            player.release()
        }
    }

}
