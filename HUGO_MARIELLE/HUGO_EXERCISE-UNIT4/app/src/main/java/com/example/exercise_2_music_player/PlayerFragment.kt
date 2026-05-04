package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class PlayerFragment : Fragment(R.layout.fragment_player) {
    private val viewModel: MusicViewModel by activityViewModels()
    private var player: ExoPlayer? = null
    private lateinit var statusTextView: TextView
    private var songName = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusTextView = view.findViewById(R.id.songTitle)

        // Automatically load the selected music via the observer
        viewModel.selectedSong.observe(viewLifecycleOwner) { songData ->
            songName = songData.substringBefore(" - ")
            val url = songData.substringAfter(" - ")

            statusTextView.text = "Loading: $songName"
            setupPlayer(url)
        }

        // --- NEW MINI PLAYER CONTROLS ---
        val playPauseBtn = view.findViewById<ImageButton>(R.id.playPauseButton)
        val prevBtn = view.findViewById<ImageButton>(R.id.prevButton)
        val nextBtn = view.findViewById<ImageButton>(R.id.nextButton)

        // Single button to toggle Play and Pause
        playPauseBtn.setOnClickListener {
            if (player?.isPlaying == true) {
                player?.pause()
            } else {
                player?.play()
            }
        }

        // Prev/Next buttons
        prevBtn.setOnClickListener { viewModel.previousSong() }
        nextBtn.setOnClickListener { viewModel.nextSong() }
    }

    private fun setupPlayer(url: String) {
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build()
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    statusTextView.text = if (isPlaying) "Playing: $songName" else "Paused: $songName"
                }
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        viewModel.nextSong()
                    }
                }
            })
        }

        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
    }
}