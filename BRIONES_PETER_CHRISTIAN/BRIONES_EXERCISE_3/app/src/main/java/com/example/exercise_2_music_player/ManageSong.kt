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
    private var songUrl = ""
    private var songName = ""

    //    setup the exoplayer
    private var player: ExoPlayer? = null

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
        val songData = intent.getStringExtra("song") ?: ""
        songName = songData.substringBefore(" - ")
        songUrl = songData.substringAfter(" - ")

//        Setup the button functions
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        songTitle = findViewById(R.id.songTitle)

//        Setup the Song title
        songTitle.text = songName

//        Setup the button listeners
        playButton.setOnClickListener {
            player?.let {
                if (!it.isPlaying && it.playbackState == Player.STATE_IDLE) {
                    it.prepare()
                }
                it.play()
            }
        }

        pauseButton.setOnClickListener {
            player?.pause()
        }

        stopButton.setOnClickListener {
            player?.let {
                it.stop()
//            Reset the music
                it.seekTo(0)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Move the ExoPlayer initialization from onCreate() to onStart()
        initializePlayer()
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(this).build().also { exoPlayer ->
                // Put the song URL to the media Item.
                val mediaItem = MediaItem.fromUri(songUrl)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()

                // Complete the addListener function
                exoPlayer.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        updateStatus()
                    }

                    override fun onPlaybackStateChanged(state: Int) {
                        updateStatus()
                    }
                })
            }
        }
    }

    private fun updateStatus() {
        val status = when {
            player?.playbackState == Player.STATE_BUFFERING -> "Buffering"
            player?.playbackState == Player.STATE_ENDED -> "Ended"
            player?.playbackState == Player.STATE_IDLE -> "Idle"
            player?.isPlaying == true -> "Playing"
            else -> "Paused"
        }
        songTitle.text = "$songName - $status"
    }

    override fun onResume() {
        super.onResume()
        // Play the music
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        // Pause the Music
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Call the release() method of the player.
        player?.release()
        player = null
    }

}
