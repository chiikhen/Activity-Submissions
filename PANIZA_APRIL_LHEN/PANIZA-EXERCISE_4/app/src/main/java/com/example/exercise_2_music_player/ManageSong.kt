/*
 * NOTE: This file is no longer used in the fragment-based implementation.
 * The functionality has been moved to MusicPlayerFragment.kt
 * This file can be safely deleted.
 */

package com.example.exercise_2_music_player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class ManageSong : AppCompatActivity() {

//    UI elements
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var songTitleTextView: TextView
    private lateinit var songStatusTextView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView

//    URL and song name retrieved from the intent
    private var songUrl = ""
    private var songName = ""

//    Setup the exoplayer
    private var player: ExoPlayer? = null

//    Handler for updating seek bar
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            player?.let {
                if (it.duration > 0) {
                    val progress = ((it.currentPosition * 100) / it.duration).toInt()
                    seekBar.progress = progress
                    currentTimeTextView.text = formatTime(it.currentPosition)
                    totalTimeTextView.text = formatTime(it.duration)
                }
            }
            handler.postDelayed(this, 1000)
        }
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

//        Retrieve the song URL from the intent
        val songItem = intent?.getStringExtra("songItem") ?: "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"

//        Setup the Song title
        songName = songItem.substringBefore(" - ").trim()

//         Put the song URL to the media Item
        songUrl = songItem.substringAfter(" - ").trim()

//        Setup the button references
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        songTitleTextView = findViewById(R.id.songTitle)
        songStatusTextView = findViewById(R.id.songStatus)
        seekBar = findViewById(R.id.seekBar)
        currentTimeTextView = findViewById(R.id.currentTime)
        totalTimeTextView = findViewById(R.id.totalTime)

//        Set the song title
        songTitleTextView.text = songName
        songStatusTextView.text = "Ready to play"

//        Setup the button functions
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
            player?.stop()
//            Reset the music
            player?.seekTo(0)
            seekBar.progress = 0
            currentTimeTextView.text = "0:00"
        }

//        Setup seek bar listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player?.let {
                        val newPosition = (it.duration * progress) / 100
                        it.seekTo(newPosition)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

//    3a.Move ExoPlayer initialization from onCreate() to onStart()
    override fun onStart() {
        super.onStart()
        initializePlayer()
        handler.post(updateSeekBarRunnable)
    }

    private fun initializePlayer() {
//        Setup the ExoPlayer
        player = ExoPlayer.Builder(this).build()

//        Put the song URL to the media Item
        val mediaItem = MediaItem.fromUri(songUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()

//        for updatingg the text view based on player's state
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songStatusTextView.text = "♪ Playing"
                } else {
                    // Not playing - could be paused or stopped
                    if (player?.playbackState == Player.STATE_READY) {
                        songStatusTextView.text = "⏸ Paused"
                    }
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> {
                        songStatusTextView.text = "⏳ Buffering..."
                    }
                    Player.STATE_READY -> {
                        if (player?.isPlaying == true) {
                            songStatusTextView.text = "♪ Playing"
                        } else {
                            songStatusTextView.text = "▶ Ready"
                        }
                    }
                    Player.STATE_IDLE -> {
                        songStatusTextView.text = "⏹ Stopped"
                    }
                    Player.STATE_ENDED -> {
                        songStatusTextView.text = "✓ Finished"
                    }
                }
            }
        })
    }

//    3c. Play the music
    override fun onResume() {
        super.onResume()
        player?.play()
    }

//    3b. Pause the music
    override fun onPause() {
        super.onPause()
        player?.pause()
    }

//    3d. Call the release() method of the player
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBarRunnable)
        player?.release()
        player = null
    }

}
