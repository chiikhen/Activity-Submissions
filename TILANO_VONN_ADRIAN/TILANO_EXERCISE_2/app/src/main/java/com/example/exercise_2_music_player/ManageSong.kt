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

//    URL retrieve from the intent

    //    setup the exoplayer
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.manage_song)

        songTitleTextView = findViewById(R.id.songTitle)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

//        Retrieve the song URL from the intent
        val songUrl = intent.getStringExtra("SONG_URL") ?: ""
        val songTitle = intent.getStringExtra("SONG_TITLE") ?: "Unknown Song"

//        Setup the button functions


//        Setup the Song title
        songTitleTextView.text = songTitle

//        Setup the ExoPlayer
        player = ExoPlayer.Builder(this).build()
//        Setup the media item to play using the URL retrieved from the intent
        val mediaItem = MediaItem.fromUri(songUrl)
        player.setMediaItem(mediaItem)
        player.prepare()

//        This listener will update the song title based on the player's state
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    // Logic when music starts playing
                    songTitleTextView.append(" (Playing)")
                } else {
                    // Logic when music is paused
                    songTitleTextView.text = songTitle // Reset to original title
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_BUFFERING) {
                    songTitleTextView.text = "Buffering..."
                }
                else if (state == Player.STATE_READY) {
                    songTitleTextView.text = songTitle
                }
                else if (state == Player.STATE_IDLE) {
                    songTitleTextView.text = "Stopped"
                }
                else if (state == Player.STATE_ENDED) {
                    songTitleTextView.text = "Playback Finished"
                }
            }
        })

//        Setup the button listeners
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
}
