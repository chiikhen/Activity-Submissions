package com.example.tabares_exercise_2

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class SongPlayerFragment : Fragment() {

    companion object {
        private const val PREFS_NAME = "MusicPlayerPrefs"
        private const val KEY_WAS_PLAYING = "wasPlaying"
        private const val KEY_PLAYBACK_POSITION = "playbackPosition"
        private const val KEY_CURRENT_SONG = "currentSong"
    }

    private lateinit var playButton: TextView
    private lateinit var pauseButton: TextView
    private lateinit var nextButton: TextView
    private lateinit var prevButton: TextView
    private lateinit var favoriteButton: TextView
    private lateinit var songTitle: TextView
    private lateinit var songStatus: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var noSongText: View
    private lateinit var playerContainer: View

    private var player: ExoPlayer? = null
    private var songUrl = ""
    private var songName = ""
    private var songArtist = ""
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false
    private var wasPlayingBeforePause = false
    private lateinit var listener: OnSongInteractionListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSongInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_song_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        nextButton = view.findViewById(R.id.nextButton)
        prevButton = view.findViewById(R.id.prevButton)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        songTitle = view.findViewById(R.id.songTitle)
        songStatus = view.findViewById(R.id.songStatus)
        seekBar = view.findViewById(R.id.seekBar)
        currentTimeText = view.findViewById(R.id.currentTime)
        totalTimeText = view.findViewById(R.id.totalTime)
        noSongText = view.findViewById(R.id.noSongText)
        playerContainer = view.findViewById(R.id.playerContainer)

        songTitle.isSelected = true  // enables marquee scrolling
        showNoSongState()
        setupButtons()
    }

    private fun setupButtons() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) { isUserSeeking = true }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val p = player
                if (p != null && p.duration > 0) {
                    p.seekTo((p.duration * (seekBar?.progress ?: 0) / 100))
                }
                isUserSeeking = false
            }
        })

        playButton.setOnClickListener {
            val p = player ?: return@setOnClickListener
            if (!p.isPlaying && p.playbackState == Player.STATE_IDLE) p.prepare()
            p.play()
            wasPlayingBeforePause = true
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
        }

        pauseButton.setOnClickListener {
            val p = player ?: return@setOnClickListener
            p.pause()
            wasPlayingBeforePause = false
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
        }

        nextButton.setOnClickListener { listener.onNextSong() }
        prevButton.setOnClickListener { listener.onPreviousSong() }

        favoriteButton.setOnClickListener {
            favoriteButton.text = if (favoriteButton.text == "♡") "♥" else "♡"
        }
    }

    fun loadSong(song: Song) {
        songName = song.name
        songArtist = song.artist
        songUrl = song.url

        showPlayerState()
        songTitle.text = songName
        songStatus.text = songArtist

        releasePlayer()
        initializePlayer()
    }

    private fun showNoSongState() {
        noSongText.visibility = View.VISIBLE
        playerContainer.visibility = View.GONE
    }

    private fun showPlayerState() {
        noSongText.visibility = View.GONE
        playerContainer.visibility = View.VISIBLE
    }

    private fun initializePlayer() {
        if (songUrl.isEmpty()) {
            songStatus.text = "Error: No URL"
            return
        }

        val ctx = context ?: return
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedSong = prefs.getString(KEY_CURRENT_SONG, "")
        val currentSongData = "$songName - $songUrl"

        wasPlayingBeforePause = if (savedSong == currentSongData) {
            prefs.getBoolean(KEY_WAS_PLAYING, false)
        } else {
            true
        }

        player = ExoPlayer.Builder(ctx)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(), true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()

        player?.let { p ->
            p.volume = 1.0f
            p.setMediaItem(MediaItem.fromUri(songUrl))
            p.prepare()

            val savedPosition = prefs.getLong(KEY_PLAYBACK_POSITION, 0L)
            if (savedSong == currentSongData && savedPosition > 0) {
                p.seekTo(savedPosition)
            }

            p.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (!isAdded) return
                    if (isPlaying) {
                        wasPlayingBeforePause = true
                        startSeekBarUpdate()
                        playButton.visibility = View.GONE
                        pauseButton.visibility = View.VISIBLE
                    } else {
                        stopSeekBarUpdate()
                        pauseButton.visibility = View.GONE
                        playButton.visibility = View.VISIBLE
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    if (!isAdded) return
                    when (state) {
                        Player.STATE_READY -> {
                            if (!p.isPlaying) {
                                p.play()
                                wasPlayingBeforePause = true
                            }
                        }
                        Player.STATE_ENDED -> {
                            wasPlayingBeforePause = false
                            stopSeekBarUpdate()
                        }
                        else -> {}
                    }
                }
            })
        }
    }

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            val p = player
            if (p != null && !isUserSeeking && p.duration > 0) {
                seekBar.progress = ((p.currentPosition * 100) / p.duration).toInt()
                currentTimeText.text = formatTime(p.currentPosition)
                totalTimeText.text = formatTime(p.duration)
            }
            handler.postDelayed(this, 100)
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun startSeekBarUpdate() = handler.post(updateSeekBarRunnable)
    private fun stopSeekBarUpdate() = handler.removeCallbacks(updateSeekBarRunnable)

    private fun savePlaybackState() {
        val p = player ?: return
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.apply {
            putBoolean(KEY_WAS_PLAYING, p.isPlaying)
            putLong(KEY_PLAYBACK_POSITION, p.currentPosition)
            putString(KEY_CURRENT_SONG, "$songName - $songUrl")
            apply()
        }
    }

    fun releasePlayer() {
        stopSeekBarUpdate()
        player?.stop()
        player?.release()
        player = null
    }

    override fun onResume() {
        super.onResume()
        val p = player
        if (p != null && wasPlayingBeforePause && p.playbackState != Player.STATE_IDLE) {
            p.play()
            startSeekBarUpdate()
        }
    }

    override fun onPause() {
        super.onPause()
        val p = player
        if (p != null) {
            if (p.isPlaying) {
                wasPlayingBeforePause = true
                p.pause()
            }
            savePlaybackState()
        }
        stopSeekBarUpdate()
    }

    override fun onStop() {
        super.onStop()
        val p = player
        if (p != null) {
            if (p.isPlaying) p.pause()
            savePlaybackState()
        }
        stopSeekBarUpdate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        releasePlayer()
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)?.edit()?.clear()?.apply()
    }
}
