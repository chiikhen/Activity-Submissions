package com.example.tabares_exercise_2

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.SeekBar
import android.widget.TextView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class ManageSong : AppCompatActivity() {

    companion object {
        private const val PREFS_NAME = "MusicPlayerPrefs"
        private const val KEY_WAS_PLAYING = "wasPlaying"
        private const val KEY_PLAYBACK_POSITION = "playbackPosition"
        private const val KEY_CURRENT_SONG = "currentSong"
    }

    private lateinit var playButton: TextView
    private lateinit var pauseButton: TextView
    private lateinit var stopButton: TextView
    private lateinit var nextButton: TextView
    private lateinit var favoriteButton: TextView
    private lateinit var songTitle: TextView
    private lateinit var songStatus: TextView
    private lateinit var backButton: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var player: ExoPlayer

    private var songUrl = ""
    private var songName = ""
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false
    private var wasPlayingBeforePause = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.manage_song)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val songData = intent.getStringExtra("SONG_DATA") ?: ""
        val parts = songData.split(" - ", limit = 2)
        songName = parts.getOrNull(0) ?: "Unknown Song"
        songUrl = parts.getOrNull(1) ?: ""

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        stopButton = findViewById(R.id.stopButton)
        nextButton = findViewById(R.id.nextButton)
        favoriteButton = findViewById(R.id.favoriteButton)
        backButton = findViewById(R.id.backButton)
        songTitle = findViewById(R.id.songTitle)
        songStatus = findViewById(R.id.songStatus)
        seekBar = findViewById(R.id.seekBar)

        songTitle.text = songName
        songStatus.text = "Ready"

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedSong = prefs.getString(KEY_CURRENT_SONG, "")
        val currentSongData = "$songName - $songUrl"

        wasPlayingBeforePause = if (savedSong == currentSongData) {
            prefs.getBoolean(KEY_WAS_PLAYING, false)
        } else {
            true
        }

        backButton.setOnClickListener {
            if (::player.isInitialized) {
                player.stop()
                player.release()
            }
            getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
            finish()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) { isUserSeeking = true }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (::player.isInitialized && player.duration > 0) {
                    player.seekTo((player.duration * (seekBar?.progress ?: 0) / 100))
                }
                isUserSeeking = false
            }
        })

        playButton.setOnClickListener {
            if (::player.isInitialized) {
                if (!player.isPlaying && player.playbackState == Player.STATE_IDLE) player.prepare()
                player.play()
                wasPlayingBeforePause = true
                songStatus.text = "Playing"
                playButton.visibility = android.view.View.GONE
                pauseButton.visibility = android.view.View.VISIBLE
            }
        }

        pauseButton.setOnClickListener {
            if (::player.isInitialized) {
                player.pause()
                wasPlayingBeforePause = false
                songStatus.text = "Paused"
                pauseButton.visibility = android.view.View.GONE
                playButton.visibility = android.view.View.VISIBLE
            }
        }

        stopButton.setOnClickListener {
            if (::player.isInitialized) {
                player.stop()
                player.seekTo(0)
                seekBar.progress = 0
                wasPlayingBeforePause = false
                songStatus.text = "Stopped"
                pauseButton.visibility = android.view.View.GONE
                playButton.visibility = android.view.View.VISIBLE
            }
        }

        nextButton.setOnClickListener {
            if (::player.isInitialized) {
                player.seekTo(0)
                songStatus.text = "Next (Restart)"
            }
        }

        initializePlayer()
    }

    private fun initializePlayer() {
        if (songUrl.isEmpty()) {
            songStatus.text = "Error: No URL"
            return
        }

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(), true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        player.volume = 1.0f
        player.setMediaItem(MediaItem.fromUri(songUrl))
        player.prepare()

        val savedPosition = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_PLAYBACK_POSITION, 0L)
        if (savedPosition > 0) player.seekTo(savedPosition)

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songStatus.text = "Playing"
                    wasPlayingBeforePause = true
                    startSeekBarUpdate()
                    playButton.visibility = android.view.View.GONE
                    pauseButton.visibility = android.view.View.VISIBLE
                } else {
                    songStatus.text = "Paused"
                    stopSeekBarUpdate()
                    pauseButton.visibility = android.view.View.GONE
                    playButton.visibility = android.view.View.VISIBLE
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> songStatus.text = "Buffering..."
                    Player.STATE_READY -> {
                        if (!player.isPlaying) {
                            player.play()
                            wasPlayingBeforePause = true
                            songStatus.text = "Playing"
                        }
                    }
                    Player.STATE_IDLE -> songStatus.text = "Idle"
                    Player.STATE_ENDED -> {
                        songStatus.text = "Ended"
                        wasPlayingBeforePause = false
                        stopSeekBarUpdate()
                    }
                }
            }
        })
    }

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            if (::player.isInitialized && !isUserSeeking && player.duration > 0) {
                seekBar.progress = ((player.currentPosition * 100) / player.duration).toInt()
            }
            handler.postDelayed(this, 100)
        }
    }

    private fun startSeekBarUpdate() = handler.post(updateSeekBarRunnable)
    private fun stopSeekBarUpdate() = handler.removeCallbacks(updateSeekBarRunnable)

    private fun savePlaybackState() {
        if (::player.isInitialized) {
            getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
                putBoolean(KEY_WAS_PLAYING, player.isPlaying)
                putLong(KEY_PLAYBACK_POSITION, player.currentPosition)
                putString(KEY_CURRENT_SONG, "$songName - $songUrl")
                apply()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::player.isInitialized && wasPlayingBeforePause) {
            player.play()
            startSeekBarUpdate()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::player.isInitialized) {
            if (player.isPlaying) {
                wasPlayingBeforePause = true
                player.pause()
            }
            savePlaybackState()
        }
        stopSeekBarUpdate()
    }

    override fun onStop() {
        super.onStop()
        if (::player.isInitialized) {
            if (player.isPlaying) player.pause()
            savePlaybackState()
        }
        stopSeekBarUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::player.isInitialized) {
            player.stop()
            player.release()
        }
        stopSeekBarUpdate()
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }

}
