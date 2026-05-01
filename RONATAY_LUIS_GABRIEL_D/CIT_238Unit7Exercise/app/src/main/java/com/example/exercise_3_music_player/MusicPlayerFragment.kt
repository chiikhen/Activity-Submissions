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

    private var listener: MusicListFragment.OnMusicInteractionListener? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBar = object : Runnable {
        override fun run() {
            val player = MusicPlayerManager.getPlayer(requireContext())
            if (player.duration > 0) {
                val progress = ((player.currentPosition * 100) / player.duration).toInt()
                seekBar.progress = progress
                currentTime.text = formatTime(player.currentPosition)
                totalTime.text = formatTime(player.duration)
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (::songStatus.isInitialized) {
                val music = MusicPlayerManager.currentMusic
                if (music != null) {
                    songStatus.text = if (isPlaying) "Now Playing" else "Paused"
                }
            }
        }

        override fun onPlaybackStateChanged(state: Int) {
            if (!::songStatus.isInitialized) return
            val player = MusicPlayerManager.getPlayer(requireContext())
            when (state) {
                Player.STATE_BUFFERING -> songStatus.text = "Buffering..."
                Player.STATE_READY -> {
                    totalTime.text = formatTime(player.duration)
                }
                Player.STATE_ENDED -> {
                    songStatus.text = "Finished"
                    seekBar.progress = 100
                }
                else -> {}
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 1000) / 60
        return String.format(Locale.US, "%d:%02d", minutes, seconds)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = (parentFragment as? MusicListFragment.OnMusicInteractionListener)
            ?: (context as? MusicListFragment.OnMusicInteractionListener)
            ?: throw RuntimeException("Host must implement OnMusicInteractionListener")
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

        updateSongInfo()

        val player = MusicPlayerManager.getPlayer(requireContext())

        playButton.setOnClickListener {
            player.play()
        }

        pauseButton.setOnClickListener {
            player.pause()
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
                    val exo = MusicPlayerManager.getPlayer(requireContext())
                    if (exo.duration > 0) {
                        val newPosition = (progress * exo.duration) / 100
                        exo.seekTo(newPosition)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onStart() {
        super.onStart()
        // Attach listener and start seekbar updates
        val player = MusicPlayerManager.getPlayer(requireContext())
        player.addListener(playerListener)
        handler.post(updateSeekBar)

        // Sync UI with current player state
        updateSongInfo()
        if (player.isPlaying) {
            songStatus.text = "Now Playing"
        } else if (MusicPlayerManager.currentMusic != null) {
            songStatus.text = "Paused"
        }
    }

    override fun onStop() {
        super.onStop()
        // Detach listener and stop seekbar updates, but DON'T release the player
        handler.removeCallbacks(updateSeekBar)
        val player = MusicPlayerManager.getPlayer(requireContext())
        player.removeListener(playerListener)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun playMusic(music: Music) {
        val ctx = context ?: return
        MusicPlayerManager.playMusic(ctx, music)
        updateSongInfo()
    }

    private fun updateSongInfo() {
        if (!::songTitle.isInitialized) return
        val music = MusicPlayerManager.currentMusic
        if (music != null) {
            songTitle.text = music.title
            songStatus.text = music.artist
        } else {
            songTitle.text = "No song selected"
            songStatus.text = "Select a song to play"
        }
    }
}
