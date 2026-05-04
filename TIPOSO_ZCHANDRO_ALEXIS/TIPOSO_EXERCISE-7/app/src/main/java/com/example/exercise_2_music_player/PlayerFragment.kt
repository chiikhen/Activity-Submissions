package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.View
import android.widget.Button
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

        // Requirement #3: Automatically load the selected music via the observer
        viewModel.selectedSong.observe(viewLifecycleOwner) { songData ->
            songName = songData.substringBefore(" - ")
            val url = songData.substringAfter(" - ")

            // Set initial text so it doesn't stay on "Sample Song"
            statusTextView.text = "Loading: $songName"
            setupPlayer(url)
        }

        // Control Listeners
        view.findViewById<Button>(R.id.playButton).setOnClickListener { player?.play() }
        view.findViewById<Button>(R.id.pauseButton).setOnClickListener { player?.pause() }

        // Stop button logic
        view.findViewById<Button>(R.id.stopButton).setOnClickListener {
            player?.stop()
            player?.seekTo(0)
            statusTextView.text = "Stopped: $songName"
        }

        // Requirement #4: Interface methods for Prev/Next
        view.findViewById<Button>(R.id.prevButton).setOnClickListener { viewModel.previousSong() }
        view.findViewById<Button>(R.id.nextButton).setOnClickListener { viewModel.nextSong() }
    }

    private fun setupPlayer(url: String) {
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build()
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    // Updates title to reflect current state
                    statusTextView.text = if (isPlaying) "Playing: $songName" else "Paused: $songName"
                }
                override fun onPlaybackStateChanged(state: Int) {
                    // Requirement #4: Automatically trigger next song when current one ends
                    if (state == Player.STATE_ENDED) {
                        viewModel.nextSong()
                    }
                }
            })
        }

        // Reset and load the new URL
        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up the player to prevent memory leaks
        player?.release()
        player = null
    }
}