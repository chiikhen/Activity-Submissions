package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayerFragment : Fragment() {

    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var songTitle: TextView

    private var player: ExoPlayer? = null
    private var listener: MusicPlayerListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicPlayerListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement MusicPlayerListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music_player, container, false)
        
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        prevButton = view.findViewById(R.id.prevButton)
        nextButton = view.findViewById(R.id.nextButton)
        songTitle = view.findViewById(R.id.songTitle)

        player = ExoPlayer.Builder(requireContext()).build()

        playButton.setOnClickListener { player?.play() }
        pauseButton.setOnClickListener { player?.pause() }
        stopButton.setOnClickListener { 
            player?.stop()
            player?.seekTo(0)
        }
        prevButton.setOnClickListener { listener?.onPreviousRequested() }
        nextButton.setOnClickListener { listener?.onNextRequested() }

        return view
    }

    fun playSong(title: String, url: String) {
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
