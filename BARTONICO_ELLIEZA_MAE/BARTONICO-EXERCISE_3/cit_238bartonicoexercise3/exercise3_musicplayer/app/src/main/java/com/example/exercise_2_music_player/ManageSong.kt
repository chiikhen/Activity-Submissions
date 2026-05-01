package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class ManageSong : Fragment(R.layout.manage_song) {

    private lateinit var player: ExoPlayer
    private lateinit var songTitleTextView: TextView
    private var listener: SelectedSongListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Link the fragment to the Activity's interface
        if (context is SelectedSongListener) listener = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songTitleTextView = view.findViewById(R.id.songTitle)
        player = ExoPlayer.Builder(requireContext()).build()

        // Button listeners using your XML IDs
        view.findViewById<Button>(R.id.playButton).setOnClickListener { player.play() }
        view.findViewById<Button>(R.id.pauseButton).setOnClickListener { player.pause() }
        view.findViewById<Button>(R.id.stopButton).setOnClickListener {
            player.stop()
            player.seekTo(0)
            player.prepare()
        }

        // Navigation button listeners (Requirement #4)
        view.findViewById<Button>(R.id.prevButton).setOnClickListener { listener?.onPreviousSong() }
        view.findViewById<Button>(R.id.nextButton).setOnClickListener { listener?.onNextSong() }
    }

    // Public function for MainActivity to trigger a song change
    fun loadNewSong(title: String, url: String) {
        songTitleTextView.text = title
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
