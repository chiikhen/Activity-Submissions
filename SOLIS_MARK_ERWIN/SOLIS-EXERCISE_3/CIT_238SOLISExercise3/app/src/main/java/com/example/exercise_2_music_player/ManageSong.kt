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
    private lateinit var songTitle: TextView


    //    URL retrieve from the intent
    private  var songUrl = ""

    //    setup the exoplayer
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

//        Retrieve the song URL from the intent
        val songData = intent.getStringExtra("EXTRA_SONG_DATA")

        if (songData != null && songData.contains(" - ")) {
            val parts = songData.split(" - ")
            val title = parts[0]
            songUrl = parts[1]
            songTitle.text = title
        } else {
            songTitle.text = "Unknown Song"
        }

        songTitle.text = title

//        Setup the button functions
//        Setup the Song title

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

                } else {
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_BUFFERING) {
                }
                if(state == Player.STATE_READY) {
                }
                if (state == Player.STATE_IDLE) {
                }
                if (state == Player.STATE_ENDED) {
                }
            }
        })

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

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

}
