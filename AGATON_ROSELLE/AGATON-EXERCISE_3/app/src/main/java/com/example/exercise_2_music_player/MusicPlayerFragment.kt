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

/**
 * Fragment that displays the music player controls and currently playing song
 */
class MusicPlayerFragment : Fragment() {

    // UI elements
    private lateinit var songTitleTextView: TextView
    private lateinit var songStatusTextView: TextView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button

    // Player
    private var player: ExoPlayer? = null

    // Current song info
    private var currentSongUrl = ""
    private var currentSongPosition = -1

    // Interface reference
    private var musicPlayerInterface: MusicPlayerInterface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicPlayerInterface) {
            musicPlayerInterface = context
        } else {
            throw RuntimeException("$context must implement MusicPlayerInterface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music_player, container, false)

        // Initialize UI elements
        songTitleTextView = view.findViewById(R.id.songTitleTextView)
        songStatusTextView = view.findViewById(R.id.songStatusTextView)
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        previousButton = view.findViewById(R.id.previousButton)
        nextButton = view.findViewById(R.id.nextButton)

        // Set initial state
        songTitleTextView.text = "No song selected"
        songStatusTextView.text = "Status: Idle"

        // Setup button listeners
        setupButtonListeners()

        return view
    }

    private fun setupButtonListeners() {
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

        stopButton.setOnClickListener {
            player?.let {
                it.stop()
                it.seekTo(0)
            }
        }

        previousButton.setOnClickListener {
            musicPlayerInterface?.onPreviousSong()
        }

        nextButton.setOnClickListener {
            musicPlayerInterface?.onNextSong()
        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build()

            // Add listener for player state changes
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        songStatusTextView.text = "Status: Playing"
                    } else {
                        if (player?.playbackState != Player.STATE_IDLE &&
                            player?.playbackState != Player.STATE_ENDED) {
                            songStatusTextView.text = "Status: Paused"
                        }
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> {
                            songStatusTextView.text = "Status: Buffering..."
                        }
                        Player.STATE_READY -> {
                            songStatusTextView.text = "Status: Ready"
                        }
                        Player.STATE_IDLE -> {
                            songStatusTextView.text = "Status: Stopped"
                        }
                        Player.STATE_ENDED -> {
                            songStatusTextView.text = "Status: Ended"
                        }
                    }
                }
            })
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    override fun onDetach() {
        super.onDetach()
        musicPlayerInterface = null
    }

    /**
     * Load and play a song
     * @param songUrl The full song string (e.g., "Song 1 - https://...")
     * @param position The position of the song in the list
     */
    fun loadSong(songUrl: String, position: Int) {
        currentSongUrl = songUrl
        currentSongPosition = position

        // Update the song title
        songTitleTextView.text = songUrl.substringBefore(" - ")

        // Extract the actual URL
        val mediaUrl = songUrl.substringAfter(" - ")

        // Setup the media item
        player?.let {
            val mediaItem = MediaItem.fromUri(mediaUrl)
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
        }
    }

    companion object {
        /**
         * Factory method to create a new instance of this fragment
         */
        fun newInstance(): MusicPlayerFragment {
            return MusicPlayerFragment()
        }
    }
}