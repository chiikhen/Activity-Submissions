package com.example.exercise_3_music_player

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

class ManageSong : Fragment() {

    private lateinit var songTitleTextView: TextView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button

    private var player: ExoPlayer? = null
    private var listener: MusicplayerListener? = null

    private var songTitle: String = "No Song Selected"
    private var songUrl: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicplayerListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement MusicPlayerListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.manage_song, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songTitleTextView = view.findViewById(R.id.songTitle)
        playButton        = view.findViewById(R.id.playButton)
        pauseButton       = view.findViewById(R.id.pauseButton)
        stopButton        = view.findViewById(R.id.stopButton)
        previousButton    = view.findViewById(R.id.previousButton)
        nextButton        = view.findViewById(R.id.nextButton)

        songTitleTextView.text = songTitle

        playButton.setOnClickListener {
            if (player?.playbackState == Player.STATE_IDLE) {
                player?.prepare()
            }
            player?.play()
        }

        pauseButton.setOnClickListener {
            player?.pause()
        }

        stopButton.setOnClickListener {
            player?.stop()
            player?.seekTo(0)
            songTitleTextView.text = "$songTitle - Stopped"
        }

        previousButton.setOnClickListener {
            listener?.onPreviousSong()
        }

        nextButton.setOnClickListener {
            listener?.onNextSong()
        }
    }

    fun loadSong(title: String, url: String) {
        songTitle = title
        songUrl   = url

        if (::songTitleTextView.isInitialized) {
            songTitleTextView.text = songTitle
        }

        player?.release()
        initPlayer()
    }

    private fun initPlayer() {
        if (songUrl.isEmpty()) return

        player = ExoPlayer.Builder(requireContext()).build()

        val mediaItem = MediaItem.fromUri(songUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()

        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songTitleTextView.text = "$songTitle - Playing"
                } else {
                    val state = player?.playbackState
                    if (state != Player.STATE_IDLE && state != Player.STATE_ENDED) {
                        songTitleTextView.text = "$songTitle - Paused"
                    }
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_IDLE || state == Player.STATE_ENDED) {
                    songTitleTextView.text = "$songTitle - Stopped"
                }
            }
        })
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