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

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var songTitle: TextView
    private lateinit var songStatus: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView

    private var songUrl = ""
    private var player: ExoPlayer? = null

    private var wasPlaying = false
    private var currentPosition: Long = 0

    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBar = object : Runnable {
        override fun run() {
            player?.let {
                if (it.duration > 0) {
                    val progress = ((it.currentPosition * 100) / it.duration).toInt()
                    seekBar.progress = progress
                    currentTime.text = formatTime(it.currentPosition)
                    totalTime.text = formatTime(it.duration)
                }
            }
            handler.postDelayed(this, 1000)
        }
    }

    private fun formatTime(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 1000) / 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.manage_song)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val songData = intent.getStringExtra("songUrl") ?: ""
        songUrl = songData.substringAfter(" - ")

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        backButton = findViewById(R.id.backButton)
        songTitle = findViewById(R.id.songTitle)
        songStatus = findViewById(R.id.songStatus)
        seekBar = findViewById(R.id.seekBar)
        currentTime = findViewById(R.id.currentTime)
        totalTime = findViewById(R.id.totalTime)

        val songName = songData.substringBefore(" - ")
        songTitle.text = songName

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getLong("currentPosition", 0)
            wasPlaying = savedInstanceState.getBoolean("wasPlaying", false)
        }

        playButton.setOnClickListener {
            player?.let {
                wasPlaying = true
                it.play()
            }
        }

        pauseButton.setOnClickListener {
            player?.let {
                wasPlaying = false
                it.pause()
            }
        }

        stopButton.setOnClickListener {
            player?.let {
                wasPlaying = false
                it.stop()
                it.seekTo(0)
                seekBar.progress = 0
                currentTime.text = "0:00"
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player?.let {
                        if (it.duration > 0) {
                            val newPosition = (progress * it.duration) / 100
                            it.seekTo(newPosition)
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onStart() {
        super.onStart()

        player = ExoPlayer.Builder(this).build()
        val mediaItem = MediaItem.fromUri(songUrl)
        player?.setMediaItem(mediaItem)

        player?.prepare()
        player?.seekTo(currentPosition)
        player?.playWhenReady = wasPlaying

        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                songStatus.text = if (isPlaying) "Now Playing" else "Paused"
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> songStatus.text = "Buffering..."
                    Player.STATE_READY -> {
                        totalTime.text = formatTime(player?.duration ?: 0)
                    }
                    Player.STATE_ENDED -> {
                        songStatus.text = "Finished"
                        seekBar.progress = 100
                    }
                }
            }
        })

        handler.post(updateSeekBar)
    }

    override fun onStop() {
        super.onStop()

        player?.let {
            currentPosition = it.currentPosition
            wasPlaying = it.isPlaying

            handler.removeCallbacks(updateSeekBar)
            it.release()
        }

        player = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("currentPosition", currentPosition)
        outState.putBoolean("wasPlaying", wasPlaying)
    }
}
