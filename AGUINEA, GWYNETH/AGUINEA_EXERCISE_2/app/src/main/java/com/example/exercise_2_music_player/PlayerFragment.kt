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

    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var statusTextView: TextView
    private lateinit var songTitleTextView: TextView

    private var player: ExoPlayer? = null
    private var communicator: SongCommunicator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SongCommunicator) {
            communicator = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)

        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        prevButton = view.findViewById(R.id.prevButton)
        nextButton = view.findViewById(R.id.nextButton)
        statusTextView = view.findViewById(R.id.statusText)
        songTitleTextView = view.findViewById(R.id.songTitle)

        playButton.setOnClickListener { player?.play() }
        pauseButton.setOnClickListener { player?.pause() }
        stopButton.setOnClickListener {
            player?.stop()
            player?.seekTo(0)
        }
        prevButton.setOnClickListener { communicator?.onPreviousRequested() }
        nextButton.setOnClickListener { communicator?.onNextRequested() }

        return view
    }

    fun playSong(songData: String) {
        val songTitle = songData.substringBefore(" - ")
        val songUrl = songData.substringAfter(" - ")
        songTitleTextView.text = songTitle

        player?.let {
            val mediaItem = MediaItem.fromUri(songUrl)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    statusTextView.text = if (isPlaying) "Status: Playing" else "Status: Paused"
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> statusTextView.text = "Status: Buffering..."
                        Player.STATE_READY -> statusTextView.text = "Status: Ready"
                        Player.STATE_IDLE -> statusTextView.text = "Status: Stopped"
                        Player.STATE_ENDED -> statusTextView.text = "Status: Finished"
                    }
                }
            })
        }
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }
}