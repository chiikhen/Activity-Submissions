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
    private lateinit var songTitle: TextView

    private var songUrl = ""
    private var songName = ""

    private lateinit var player: ExoPlayer

    private var wasPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.manage_song)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        songUrl = intent.getStringExtra("SONG_URL") ?: ""

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        songTitle = findViewById(R.id.songTitle)

        songName = songUrl.substringBefore(" - ")
        songTitle.text = songName

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

        player = ExoPlayer.Builder(this).build()
        val actualUrl = songUrl.substringAfter(" - ")
        val mediaItem = MediaItem.fromUri(actualUrl)
        player.setMediaItem(mediaItem)
        player.prepare()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songTitle.text = "$songName - Playing"
                } else {
                    songTitle.text = "$songName - Paused"
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_BUFFERING) {
                    songTitle.text = "$songName - Buffering..."
                }
                if (state == Player.STATE_READY) {
                    songTitle.text = "$songName - Ready"
                    if (wasPlaying) {
                        player.play()
                        wasPlaying = false
                    }
                }
                if (state == Player.STATE_IDLE) {
                    songTitle.text = "$songName - Idle"
                }
                if (state == Player.STATE_ENDED) {
                    songTitle.text = "$songName - Ended"
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        wasPlaying = player.isPlaying
        player.pause()
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
