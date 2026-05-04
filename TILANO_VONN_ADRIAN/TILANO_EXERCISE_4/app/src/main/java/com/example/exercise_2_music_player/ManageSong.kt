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

    private var player: ExoPlayer? = null
    private lateinit var songTitleTextView: TextView
    private var listener: SelectedSongListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SelectedSongListener) listener = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songTitleTextView = view.findViewById(R.id.songTitle)

        // Initialize ExoPlayer safely
        player = ExoPlayer.Builder(requireContext()).build()

        // Play/Pause/Stop Buttons
        view.findViewById<Button>(R.id.playButton).setOnClickListener { player?.play() }
        view.findViewById<Button>(R.id.pauseButton).setOnClickListener { player?.pause() }
        view.findViewById<Button>(R.id.stopButton).setOnClickListener {
            player?.stop()
            player?.seekTo(0)
        }

        // Navigation buttons (Communicates back to Activity)
        view.findViewById<Button>(R.id.prevButton).setOnClickListener { listener?.onPreviousSong() }
        view.findViewById<Button>(R.id.nextButton).setOnClickListener { listener?.onNextSong() }
    }

    // This is called by MainActivity when a song is selected or changed
    fun loadNewSong(title: String, url: String) {
        songTitleTextView.text = title
        val mediaItem = MediaItem.fromUri(url)
        player?.let {
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Release player when the view is destroyed to save memory
        player?.release()
        player = null
    }
}