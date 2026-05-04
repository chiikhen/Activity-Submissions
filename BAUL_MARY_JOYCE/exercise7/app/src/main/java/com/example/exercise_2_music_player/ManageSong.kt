package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class ManageSong : Fragment() {

    private lateinit var playPauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var favButton: Button
    private lateinit var songTitleTextView: TextView

    private var player: ExoPlayer? = null
    private var listener: MusicNavigationListener? = null

    private var currentSongTitle = ""
    private var listenerAdded = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicNavigationListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.manage_song, container, false)

        playPauseButton = view.findViewById(R.id.playPauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        prevButton = view.findViewById(R.id.prevButton)
        nextButton = view.findViewById(R.id.nextButton)
        favButton = view.findViewById(R.id.favButton)
        songTitleTextView = view.findViewById(R.id.songTitle)

        player = (activity as MainActivity).getPlayerInstance()

        setupPlayerListener()

        playPauseButton.setOnClickListener {
            player?.let {
                if (it.isPlaying) {
                    it.pause()
                } else {
                    it.play()
                }
            }
        }

        // ✅ FIXED STOP BUTTON (no manual UI updates)
        stopButton.setOnClickListener {
            player?.pause()
            player?.seekTo(0)
        }

        prevButton.setOnClickListener {
            listener?.onPreviousPressed()
        }

        nextButton.setOnClickListener {
            listener?.onNextPressed()
        }

        // ⭐ FAVORITE BUTTON
        favButton.setOnClickListener {
            val song = currentSongTitle

            if (song.isBlank()) return@setOnClickListener

            if (FavoritesFragment.favoriteSongs.contains(song)) {
                FavoritesFragment.favoriteSongs.remove(song)
                favButton.text = "♡"
            } else {
                FavoritesFragment.favoriteSongs.add(song)
                favButton.text = "♥"
            }
        }

        return view
    }

    // ✅ SAFE PLAYER LISTENER (NO MEMORY LEAK)
    private val playerListener = object : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            playPauseButton.text = if (isPlaying) "PAUSE" else "PLAY"

            songTitleTextView.text = if (isPlaying) {
                "Playing: $currentSongTitle"
            } else {
                "Paused: $currentSongTitle"
            }
        }
    }

    private fun setupPlayerListener() {
        if (listenerAdded) return
        listenerAdded = true

        player?.addListener(playerListener)
    }

    fun updateUI(songTitle: String) {
        currentSongTitle = songTitle
        songTitleTextView.text = "Playing: $songTitle"

        favButton.text = if (FavoritesFragment.favoriteSongs.contains(songTitle)) {
            "♥"
        } else {
            "♡"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        player?.removeListener(playerListener)

        listener = null
        listenerAdded = false
    }
}