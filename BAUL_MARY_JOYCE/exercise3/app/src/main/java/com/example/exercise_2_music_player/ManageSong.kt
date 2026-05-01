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

class ManageSong : Fragment() {

    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var songTitleTextView: TextView

    private var songUrl = ""
    private var currentSongTitle = ""
    private lateinit var player: ExoPlayer
    private var listener: MusicNavigationListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicNavigationListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.manage_song, container, false)

        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        prevButton = view.findViewById(R.id.prevButton)
        nextButton = view.findViewById(R.id.nextButton)
        songTitleTextView = view.findViewById(R.id.songTitle)

        player = ExoPlayer.Builder(requireContext()).build()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songTitleTextView.text = "Playing $currentSongTitle"
                } else if (player.playbackState != Player.STATE_IDLE) {
                    songTitleTextView.text = "$currentSongTitle Paused"
                }
            }
        })

        playButton.setOnClickListener {
            if (!player.isPlaying && player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            player.play()
        }

        pauseButton.setOnClickListener { player.pause() }

        stopButton.setOnClickListener {
            player.stop()
            player.seekTo(0)
            songTitleTextView.text = "$currentSongTitle Stopped"
        }

        prevButton.setOnClickListener { listener?.onPreviousPressed() }
        nextButton.setOnClickListener { listener?.onNextPressed() }

        return view
    }

    fun playMusic(songTitle: String, url: String) {
        currentSongTitle = songTitle
        songUrl = url
        songTitleTextView.text = songTitle
        val mediaItem = MediaItem.fromUri(songUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}