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

class SongPlayerFragment : Fragment() {

    interface Callback {
        fun onPreviousRequested()
        fun onNextRequested()
    }

    private var callback: Callback? = null

    private lateinit var songTitleTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var previousButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var nextButton: ImageButton

    private var songUrl = ""
    private var songName = ""
    private var pendingSongData: String? = null
    private var pendingAutoPlay = false
    private var isViewReady = false

    private lateinit var player: ExoPlayer

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? Callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(ARG_SONG_DATA)?.let { setSongInternal(it, false) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_song_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songTitleTextView = view.findViewById(R.id.songTitleTextView)
        statusTextView = view.findViewById(R.id.statusTextView)
        previousButton = view.findViewById(R.id.previousButton)
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        nextButton = view.findViewById(R.id.nextButton)

        isViewReady = true
        songTitleTextView.text = songName.ifEmpty { "Song" }
        statusTextView.text = "Idle"

        val pendingData = pendingSongData
        if (pendingData != null) {
            applySong(pendingData, pendingAutoPlay)
            pendingSongData = null
            pendingAutoPlay = false
        }

        previousButton.setOnClickListener { callback?.onPreviousRequested() }
        nextButton.setOnClickListener { callback?.onNextRequested() }

        playButton.setOnClickListener {
            if (this::player.isInitialized && player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            if (this::player.isInitialized) {
                player.play()
            }
        }

        pauseButton.setOnClickListener {
            if (this::player.isInitialized) {
                player.pause()
            }
        }

        stopButton.setOnClickListener {
            if (this::player.isInitialized) {
                player.stop()
                player.seekTo(0)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
        val dataToApply = pendingSongData ?: if (songUrl.isNotEmpty()) {
            "$songName - $songUrl"
        } else {
            null
        }
        if (dataToApply != null) {
            applySong(dataToApply, pendingAutoPlay)
            pendingSongData = null
            pendingAutoPlay = false
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::player.isInitialized) {
            player.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::player.isInitialized) {
            player.release()
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    fun setSong(songData: String, autoPlay: Boolean) {
        if (!isAdded || !isViewReady) {
            pendingSongData = songData
            pendingAutoPlay = autoPlay
            return
        }
        applySong(songData, autoPlay)
    }

    private fun applySong(songData: String, autoPlay: Boolean) {
        setSongInternal(songData, autoPlay)
        if (!this::songTitleTextView.isInitialized) {
            pendingSongData = songData
            pendingAutoPlay = autoPlay
            return
        }
        songTitleTextView.text = songName
        if (this::player.isInitialized) {
            val mediaItem = MediaItem.fromUri(songUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            if (autoPlay) {
                player.play()
            }
        } else {
            pendingSongData = songData
            pendingAutoPlay = autoPlay
        }
    }

    private fun setSongInternal(songData: String, autoPlay: Boolean) {
        songName = songData.substringBefore(" - ")
        songUrl = songData.substringAfter(" - ")
        if (!this::player.isInitialized) {
            pendingSongData = songData
            pendingAutoPlay = autoPlay
        }
    }

    private fun initializePlayer() {
        if (this::player.isInitialized) {
            return
        }
        player = ExoPlayer.Builder(requireContext()).build()
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                statusTextView.text = if (isPlaying) "Playing" else "Paused"
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> statusTextView.text = "Buffering..."
                    Player.STATE_READY -> if (!player.isPlaying) statusTextView.text = "Ready"
                    Player.STATE_IDLE -> statusTextView.text = "Idle"
                    Player.STATE_ENDED -> statusTextView.text = "Ended"
                }
            }
        })
    }

    companion object {
        private const val ARG_SONG_DATA = "ARG_SONG_DATA"

        fun newInstance(initialSongData: String): SongPlayerFragment {
            val fragment = SongPlayerFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_SONG_DATA, initialSongData)
            }
            return fragment
        }
    }
}
