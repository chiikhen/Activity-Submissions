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
    private lateinit var stopButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var songTitle: TextView

    private var player: ExoPlayer? = null
    private var songUrl = ""
    private var songName = "Select a song"
    private var wasPlaying = false
    private var playbackPosition: Long = 0

    private var listener: OnSongSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongSelectedListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        previousButton = view.findViewById(R.id.previousButton)
        nextButton = view.findViewById(R.id.nextButton)
        songTitle = view.findViewById(R.id.songTitle)

        initializePlayer()

        playButton.setOnClickListener {
            player?.let {
                if (!it.isPlaying && it.playbackState == Player.STATE_IDLE) {
                    it.prepare()
                }
                wasPlaying = true
                it.play()
            }
        }

        pauseButton.setOnClickListener {
            wasPlaying = false
            player?.pause()
        }

        stopButton.setOnClickListener {
            wasPlaying = false
            playbackPosition = 0
            player?.stop()
            player?.seekTo(0)
        }

        previousButton.setOnClickListener {
            listener?.onPreviousSong()
        }

        nextButton.setOnClickListener {
            listener?.onNextSong()
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songTitle.text = "$songName - Playing"
                } else {
                    songTitle.text = "$songName - Paused"
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> songTitle.text = "$songName - Buffering..."
                    Player.STATE_READY -> songTitle.text = "$songName - Ready"
                    Player.STATE_IDLE -> songTitle.text = "$songName - Stopped"
                    Player.STATE_ENDED -> songTitle.text = "$songName - Ended"
                }
            }
        })
    }

    fun loadSong(songData: String) {
        songUrl = songData.substringAfter(" - ")
        songName = songData.substringBefore(" - ")
        songTitle.text = songName

        wasPlaying = false
        playbackPosition = 0

        player?.stop()
        player?.clearMediaItems()

        val mediaItem = MediaItem.fromUri(songUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        wasPlaying = true
    }

    override fun onPause() {
        super.onPause()
        player?.let {
            playbackPosition = it.currentPosition
            wasPlaying = it.isPlaying
            it.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (wasPlaying) {
            player?.play()
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
