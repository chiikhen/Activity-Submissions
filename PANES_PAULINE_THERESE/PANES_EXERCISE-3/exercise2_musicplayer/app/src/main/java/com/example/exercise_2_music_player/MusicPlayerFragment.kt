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

class MusicPlayerFragment : Fragment() {

    interface OnNavigationListener {
        fun onPreviousSong()
        fun onNextSong()
    }

    private var navigationListener: OnNavigationListener? = null

    // UI
    private lateinit var songTitleTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button

    // Player
    private var player: ExoPlayer? = null
    private var currentSongUrl: String = ""
    private var currentSongName: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNavigationListener) {
            navigationListener = context
        } else {
            throw RuntimeException("$context must implement OnNavigationListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.music_player_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songTitleTextView = view.findViewById(R.id.songTitleTextView)
        statusTextView = view.findViewById(R.id.statusTextView)
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        previousButton = view.findViewById(R.id.previousButton)
        nextButton = view.findViewById(R.id.nextButton)

        playButton.setOnClickListener {
            player?.let {
                if (it.playbackState == Player.STATE_IDLE) it.prepare()
                it.play()
            }
        }

        pauseButton.setOnClickListener {
            player?.pause()
        }

        stopButton.setOnClickListener {
            player?.stop()
            player?.seekTo(0)
        }

        previousButton.setOnClickListener {
            navigationListener?.onPreviousSong()
        }

        nextButton.setOnClickListener {
            navigationListener?.onNextSong()
        }
    }

    // Called from MainActivity when a song is selected
    fun loadSong(songData: String) {
        val songName = songData.substringBefore(" - ")
        val songUrl = songData.substringAfter(" - ")

        currentSongName = songName
        currentSongUrl = songUrl

        songTitleTextView.text = songName

        // Release old player and init new one
        player?.release()
        initializePlayer(songUrl)
    }

    private fun initializePlayer(url: String) {
        player = ExoPlayer.Builder(requireContext()).build()

        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()

        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                statusTextView.text = if (isPlaying) "Status: Playing" else "Status: Paused"
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> statusTextView.text = "Status: Buffering..."
                    Player.STATE_READY -> {
                        if (player?.isPlaying == false) statusTextView.text = "Status: Ready"
                    }
                    Player.STATE_IDLE -> statusTextView.text = "Status: Idle"
                    Player.STATE_ENDED -> statusTextView.text = "Status: Ended"
                }
            }
        })

        player?.play()
    }

    override fun onStart() {
        super.onStart()
        // Re-init player if we have a URL but player was released
        if (currentSongUrl.isNotEmpty() && player == null) {
            initializePlayer(currentSongUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (currentSongUrl.isNotEmpty()) {
            player?.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    override fun onDetach() {
        super.onDetach()
        navigationListener = null
    }
}