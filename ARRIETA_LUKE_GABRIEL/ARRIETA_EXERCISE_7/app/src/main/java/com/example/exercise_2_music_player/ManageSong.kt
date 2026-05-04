package com.example.exercise_2_music_player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.SeekBar
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
    private lateinit var songStatus: TextView
    private lateinit var backButton: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView

//    URL retrieve from the intent
    private  var songUrl = ""
    private  var songName = ""

    //    setup the exoplayer
    private lateinit var player: ExoPlayer

    //    Handler for updating seekbar
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false
    private var wasPlaying = false  // Track if song was playing before pause

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
        val songData = intent.getStringExtra("SONG_DATA") ?: ""
        songUrl = songData.substringAfter(" - ")
        songName = songData.substringBefore(" - ")

//        Setup the button functions
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        backButton = findViewById(R.id.backButton)

//        Setup the Song title and status
        songTitle = findViewById(R.id.songTitle)
        songStatus = findViewById(R.id.songStatus)
        songTitle.text = songName
        songStatus.text = "Ready"

//        Setup SeekBar and time labels
        seekBar = findViewById(R.id.seekBar)
        currentTime = findViewById(R.id.currentTime)
        totalTime = findViewById(R.id.totalTime)

//        Setup back button listener
        backButton.setOnClickListener {
            finish()
        }

//        Setup SeekBar listener to allow user to seek through the song
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && ::player.isInitialized) {
                    val duration = player.duration
                    if (duration > 0) {
                        val position = (duration * progress / 100).toLong()
                        currentTime.text = formatTime(position)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (::player.isInitialized) {
                    val duration = player.duration
                    if (duration > 0) {
                        val position = (duration * (seekBar?.progress ?: 0) / 100).toLong()
                        player.seekTo(position)
                    }
                }
                isUserSeeking = false
            }
        })

//        Setup the button listeners
        playButton.setOnClickListener {
            if (::player.isInitialized) {
                if (!player.isPlaying && player.playbackState == Player.STATE_IDLE) {
                    player.prepare()
                }
                player.play()
                wasPlaying = true  // User manually started playback
            }
        }

        pauseButton.setOnClickListener {
            if (::player.isInitialized) {
                player.pause()
                wasPlaying = false  // User manually paused, don't auto-resume
            }
        }

        stopButton.setOnClickListener {
            if (::player.isInitialized) {
                player.stop()
//            Reset the music
                player.seekTo(0)
                seekBar.progress = 0
                currentTime.text = "0:00"
                wasPlaying = false  // Stopped, don't auto-resume
            }
        }

//        Setup the ExoPlayer once in onCreate (not in onStart to preserve position)
        initializePlayer()
    }

    private fun initializePlayer() {
//        Setup the ExoPlayer
        player = ExoPlayer.Builder(this).build()
//        Setup the media item to play using the URL retrieved from the intent
        val mediaItem = MediaItem.fromUri(songUrl)
        player.setMediaItem(mediaItem)
        player.prepare()

//        This listener will update the song status based on the player's state
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // Don't update wasPlaying here - it's managed by lifecycle methods
                if (isPlaying) {
                    songStatus.text = "Playing"
                    startSeekBarUpdate()
                } else {
                    songStatus.text = "Paused"
                    stopSeekBarUpdate()
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> songStatus.text = "Buffering..."
                    Player.STATE_READY -> {
                        songStatus.text = "Ready"
                        // Update total time when ready
                        totalTime.text = formatTime(player.duration)
                        // Auto-start playback when song is ready (first time loading)
                        if (player.currentPosition == 0L && !wasPlaying) {
                            player.play()
                            wasPlaying = true  // Mark as playing after auto-start
                        }
                    }
                    Player.STATE_IDLE -> songStatus.text = "Idle"
                    Player.STATE_ENDED -> {
                        songStatus.text = "Ended"
                        stopSeekBarUpdate()
                        wasPlaying = false
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
//        Player is now initialized in onCreate, not here
    }

//    Function to format milliseconds to mm:ss format
    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000).toInt()
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }

//    Function to update seekbar continuously
    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            if (::player.isInitialized && !isUserSeeking) {
                val duration = player.duration
                val currentPosition = player.currentPosition

                if (duration > 0) {
                    val progress = ((currentPosition * 100) / duration).toInt()
                    seekBar.progress = progress
                    currentTime.text = formatTime(currentPosition)
                }
            }
            handler.postDelayed(this, 100) // Update every 100ms
        }
    }

    private fun startSeekBarUpdate() {
        handler.post(updateSeekBarRunnable)
    }

    private fun stopSeekBarUpdate() {
        handler.removeCallbacks(updateSeekBarRunnable)
    }

    override fun onResume() {
        super.onResume()
//        Auto-resume playback if it was playing before (e.g., returning from home button)
        if (::player.isInitialized && wasPlaying) {
            player.play()
        }
    }

    override fun onPause() {
        super.onPause()
//        Save playing state BEFORE pausing (so we can resume later)
        if (::player.isInitialized) {
            wasPlaying = player.isPlaying  // Save state before pausing
            player.pause()
        }
        stopSeekBarUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
//        Release the player
        if (::player.isInitialized) {
            player.release()
        }
        stopSeekBarUpdate()
    }

}
