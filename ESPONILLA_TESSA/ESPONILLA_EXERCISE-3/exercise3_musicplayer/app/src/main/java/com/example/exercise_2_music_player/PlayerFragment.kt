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
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class PlayerFragment : Fragment() {

    private var listener: OnSongSelectedListener? = null
    private lateinit var player: ExoPlayer

    private lateinit var songTitleTextView: TextView
    private lateinit var songStatusTextView: TextView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnSongSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_player, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        songTitleTextView = view.findViewById(R.id.songTitleTextView)
        songStatusTextView = view.findViewById(R.id.songStatusTextView)
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        prevButton = view.findViewById(R.id.prevButton)
        nextButton = view.findViewById(R.id.nextButton)

        player = ExoPlayer.Builder(requireContext()).build()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) songStatusTextView.text = "Playing"
                else if (player.playbackState != Player.STATE_IDLE) songStatusTextView.text = "Paused"
            }

            override fun onPlaybackStateChanged(state: Int) {
                songStatusTextView.text = when (state) {
                    Player.STATE_BUFFERING -> "Buffering..."
                    Player.STATE_READY -> "Ready"
                    Player.STATE_IDLE -> "Idle"
                    Player.STATE_ENDED -> "Ended"
                    else -> ""
                }
            }
        })

        playButton.setOnClickListener {
            when (player.playbackState) {
                Player.STATE_IDLE -> { player.prepare(); player.play() }
                Player.STATE_ENDED -> { player.seekTo(0); player.play() }
                else -> player.play()
            }
        }

        pauseButton.setOnClickListener { player.pause() }

        stopButton.setOnClickListener {
            player.stop()
            player.seekTo(0)
        }

        prevButton.setOnClickListener { listener?.onPreviousSong() }
        nextButton.setOnClickListener { listener?.onNextSong() }
    }

    fun loadSong(title: String, url: String) {
        songTitleTextView.text = title
        player.stop()
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}