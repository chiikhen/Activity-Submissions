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

class NowPlayingFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
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
        }
    }

    private fun setupButtonListeners() {
        playButton.setOnClickListener {
            player?.play()
        }

        pauseButton.setOnClickListener {
            player?.pause()
        }

        stopButton.setOnClickListener {
            player?.stop()
        }

        previousButton.setOnClickListener {
            // Handle previous
        }

        nextButton.setOnClickListener {
            // Handle next
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