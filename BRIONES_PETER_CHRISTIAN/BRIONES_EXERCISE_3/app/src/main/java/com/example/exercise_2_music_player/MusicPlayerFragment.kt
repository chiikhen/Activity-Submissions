package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayerFragment : Fragment() {

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var songTitle: TextView

    private var songUrl = ""
    private var songName = ""
    private var player: ExoPlayer? = null
    private var listener: MusicPlayerInterface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicPlayerInterface) {
            listener = context
        } else {
            throw RuntimeException("$context must implement MusicPlayerInterface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music_player, container, false)

        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        nextButton = view.findViewById(R.id.nextButton)
        prevButton = view.findViewById(R.id.prevButton)
        songTitle = view.findViewById(R.id.songTitle)

        playButton.setOnClickListener {
            player?.let {
                if (!it.isPlaying && it.playbackState == Player.STATE_IDLE) {
                    it.prepare()
                }
                it.play()
            }
        }

        pauseButton.setOnClickListener {
            player?.pause()
        }

        nextButton.setOnClickListener {
            listener?.onNextRequested()
        }

        prevButton.setOnClickListener {
            listener?.onPreviousRequested()
        }

        return view
    }

    fun playSong(songData: String) {
        songName = songData.substringBefore(" - ")
        songUrl = songData.substringAfter(" - ")
        
        songTitle.text = songName
        
        if (player == null) {
            initializePlayer()
        }
        
        player?.let {
            val mediaItem = MediaItem.fromUri(songUrl)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updateStatus()
                }
                override fun onPlaybackStateChanged(state: Int) {
                    updateStatus()
                }
            })
        }
    }

    private fun updateStatus() {
        if (!isAdded) return
        val status = when {
            player?.playbackState == Player.STATE_BUFFERING -> "Buffering"
            player?.playbackState == Player.STATE_ENDED -> "Ended"
            player?.playbackState == Player.STATE_IDLE -> "Idle"
            player?.isPlaying == true -> "Playing"
            else -> "Paused"
        }
        songTitle.text = "$songName - $status"
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
