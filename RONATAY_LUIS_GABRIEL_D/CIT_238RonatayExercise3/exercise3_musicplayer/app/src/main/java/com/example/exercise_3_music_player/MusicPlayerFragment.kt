package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import java.util.Locale

class MusicPlayerFragment : Fragment() {

    private lateinit var songTitle: TextView
    private lateinit var songStatus: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton

    private var player: ExoPlayer? = null
    private var currentMusic: Music? = null
    private var wasPlaying = false
    private var currentPosition: Long = 0
    private var listener: MusicListFragment.OnMusicInteractionListener? = null

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
        return String.format(Locale.US, "%d:%02d", minutes, seconds)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicListFragment.OnMusicInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnMusicInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songTitle = view.findViewById(R.id.playerSongTitle)
        songStatus = view.findViewById(R.id.playerSongStatus)
        seekBar = view.findViewById(R.id.playerSeekBar)
        currentTime = view.findViewById(R.id.playerCurrentTime)
        totalTime = view.findViewById(R.id.playerTotalTime)
        playButton = view.findViewById(R.id.playerPlayButton)
        pauseButton = view.findViewById(R.id.playerPauseButton)
        prevButton = view.findViewById(R.id.playerPrevButton)
        nextButton = view.findViewById(R.id.playerNextButton)

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getLong("currentPosition", 0)
            wasPlaying = savedInstanceState.getBoolean("wasPlaying", false)
            val title = savedInstanceState.getString("musicTitle")
            val url = savedInstanceState.getString("musicUrl")
            if (title != null && url != null) {
                currentMusic = Music(title, url)
            }
        }

        updateSongInfo()

        playButton.setOnClickListener {
            player?.play()
        }

        pauseButton.setOnClickListener {
            player?.pause()
        }

        prevButton.setOnClickListener {
            listener?.onPreviousRequested()
        }

        nextButton.setOnClickListener {
            listener?.onNextRequested()
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
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("currentPosition", currentPosition)
        outState.putBoolean("wasPlaying", wasPlaying)
        currentMusic?.let {
            outState.putString("musicTitle", it.title)
            outState.putString("musicUrl", it.url)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun playMusic(music: Music) {
        currentMusic = music
        currentPosition = 0
        wasPlaying = true
        updateSongInfo()

        player?.let { exo ->
            exo.stop()
            exo.setMediaItem(MediaItem.fromUri(music.url))
            exo.prepare()
            exo.seekTo(0)
            exo.playWhenReady = true
        }
    }

    private fun updateSongInfo() {
        if (!::songTitle.isInitialized) return
        currentMusic?.let {
            songTitle.text = it.title
        } ?: run {
            songTitle.text = "No song selected"
            songStatus.text = "Select a song to play"
        }
    }

    private fun initializePlayer() {
        val context = context ?: return
        player = ExoPlayer.Builder(context).build()

        currentMusic?.let { music ->
            player?.setMediaItem(MediaItem.fromUri(music.url))
            player?.prepare()
            player?.seekTo(currentPosition)
            player?.playWhenReady = wasPlaying
        }

        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (::songStatus.isInitialized) {
                    songStatus.text = if (isPlaying) "Now Playing" else "Paused"
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (!::songStatus.isInitialized) return
                when (state) {
                    Player.STATE_BUFFERING -> songStatus.text = "Buffering..."
                    Player.STATE_READY -> {
                        totalTime.text = formatTime(player?.duration ?: 0)
                    }
                    Player.STATE_ENDED -> {
                        songStatus.text = "Finished"
                        seekBar.progress = 100
                    }
                    else -> {}
                }
            }
        })

        handler.post(updateSeekBar)
    }

    private fun releasePlayer() {
        player?.let {
            currentPosition = it.currentPosition
            wasPlaying = it.isPlaying
            handler.removeCallbacks(updateSeekBar)
            it.release()
        }
        player = null
    }
}
