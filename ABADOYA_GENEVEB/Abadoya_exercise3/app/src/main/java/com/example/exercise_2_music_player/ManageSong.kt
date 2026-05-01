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

    //    UI elements
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var songTitleTextView: TextView
    private lateinit var songStatusTextView: TextView

    //    URL retrieved from the intent
    private var songUrl = ""

    //    Setup the ExoPlayer
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

//        Retrieve the song URL from the intent
        songUrl = intent.getStringExtra("SONG_URL") ?: ""

//        Setup the button references
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        songTitleTextView = findViewById(R.id.songTitleTextView)
        songStatusTextView = findViewById(R.id.songStatusTextView)

//        Setup the Song title using substringBefore to get only the name (before " - ")
        songTitleTextView.text = songUrl.substringBefore(" - ")

//        Setup the button listeners
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
//            Reset the music
            player.seekTo(0)
        }
    }

    override fun onStart() {
        super.onStart()

//        Moved ExoPlayer initialization to onStart()
        player = ExoPlayer.Builder(this).build()

//        Put the song URL to the media item
//        Extract the actual URL from the song string (the part after " - ")
        val mediaUrl = songUrl.substringAfter(" - ")
        val mediaItem = MediaItem.fromUri(mediaUrl)
        player.setMediaItem(mediaItem)
        player.prepare()

//        Complete the addListener function to update the TextView with player state
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songStatusTextView.text = "Status: Playing"
                } else {
                    // Only show Paused if not in IDLE or ENDED state (those are handled below)
                    if (player.playbackState != Player.STATE_IDLE &&
                        player.playbackState != Player.STATE_ENDED) {
                        songStatusTextView.text = "Status: Paused"
                    }
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_BUFFERING) {
                    songStatusTextView.text = "Status: Buffering..."
                }
                if (state == Player.STATE_READY) {
                    songStatusTextView.text = "Status: Ready"
                }
                if (state == Player.STATE_IDLE) {
                    songStatusTextView.text = "Status: Stopped"
                }
                if (state == Player.STATE_ENDED) {
                    songStatusTextView.text = "Status: Ended"
                }
            }
        })
    }

    //   Pause the music when the activity is paused
    override fun onPause() {
        super.onPause()
        player.pause()
    }

    //    Play the music when the activity is resumed
    override fun onResume() {
        super.onResume()
        player.play()
    }

    //    Release the player when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

}