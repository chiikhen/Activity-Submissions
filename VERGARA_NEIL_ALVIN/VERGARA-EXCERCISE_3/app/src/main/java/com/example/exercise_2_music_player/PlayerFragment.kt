package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerFragment : Fragment() {
    private lateinit var songTitle: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var waveformView: WaveformView
    private lateinit var statusTextView: TextView

    private var player: ExoPlayer? = null
    private var currentSongUrl = ""
    private var currentSongTitle = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songTitle = view.findViewById(R.id.songTitle)
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        previousButton = view.findViewById(R.id.previousButton)
        nextButton = view.findViewById(R.id.nextButton)
        waveformView = view.findViewById(R.id.waveformView)
        statusTextView = view.findViewById(R.id.statusTextView)

        initializePlayer()
        setupButtonListeners()
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build()
            player?.addListener(object : androidx.media3.common.Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    statusTextView.text = if (isPlaying) "Now Playing..." else "Paused"
                    waveformView.setIsPlaying(isPlaying)
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        androidx.media3.common.Player.STATE_BUFFERING -> {
                            statusTextView.text = "Buffering..."
                        }
                        androidx.media3.common.Player.STATE_READY -> {
                            statusTextView.text = "Ready to Play"
                        }
                        androidx.media3.common.Player.STATE_ENDED -> {
                            statusTextView.text = "Playback Ended"
                        }
                    }
                }
            })
        }
    }

    private fun setupButtonListeners() {
        playButton.setOnClickListener {
            if (currentSongUrl.isNotEmpty()) {
                if (player?.isPlaying == false) {
                    player?.play()
                }
            }
        }

        pauseButton.setOnClickListener {
            player?.pause()
        }

        stopButton.setOnClickListener {
            player?.stop()
            player?.seekTo(0)
        }

        previousButton.setOnClickListener {
            (activity as? MusicPlayerListener)?.onPreviousSong()
        }

        nextButton.setOnClickListener {
            (activity as? MusicPlayerListener)?.onNextSong()
        }
    }

    fun setSong(title: String, url: String) {
        currentSongTitle = title
        currentSongUrl = url
        songTitle.text = title

        player?.let {
            val mediaItem = MediaItem.fromUri(url)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
    }
}