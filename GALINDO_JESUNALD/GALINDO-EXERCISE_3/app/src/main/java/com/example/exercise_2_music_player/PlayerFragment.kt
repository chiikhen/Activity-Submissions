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

    private lateinit var listener: SongControlListener
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button
    private lateinit var songTitleTextView: TextView
    private lateinit var statusTextView: TextView

    private var player: ExoPlayer? = null
    private var currentSongPosition: Int = -1

    // 1. Attach the interface listener to the hosting Activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SongControlListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement SongControlListener")
        }
    }

    // 2. Link the UI layout to this fragment
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
        songTitleTextView = view.findViewById(R.id.songTitleTextView)
        statusTextView = view.findViewById(R.id.statusTextView)

        setupButtons()
        return view
    }

    // 3. Initialize ExoPlayer when the fragment starts
    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    // 4. Release ExoPlayer to free up memory when the fragment stops
    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }

    // 5. Setup the click listeners for all music controls
    private fun setupButtons() {
        playButton.setOnClickListener {
            if (player?.playbackState == Player.STATE_IDLE) {
                player?.prepare()
            }
            player?.play()
        }

        pauseButton.setOnClickListener { player?.pause() }

        stopButton.setOnClickListener {
            player?.stop()
            player?.seekTo(0)
        }

        prevButton.setOnClickListener {
            // Tell the MainActivity to load the previous song
            if (currentSongPosition != -1) listener.onPreviousSong(currentSongPosition)
        }

        nextButton.setOnClickListener {
            // Tell the MainActivity to load the next song
            if (currentSongPosition != -1) listener.onNextSong(currentSongPosition)
        }
    }

    // 6. Build the player and attach the state listener
    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build()

            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    // Using getString() to fetch text from strings.xml
                    statusTextView.text = if (isPlaying) {
                        getString(R.string.status_playing)
                    } else {
                        getString(R.string.status_paused)
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    // Using getString() to fetch text from strings.xml
                    when (state) {
                        Player.STATE_BUFFERING -> statusTextView.text = getString(R.string.status_buffering)
                        Player.STATE_READY -> {
                            if (player?.isPlaying == false) {
                                statusTextView.text = getString(R.string.status_ready)
                            }
                        }
                        Player.STATE_IDLE -> statusTextView.text = getString(R.string.status_idle)
                        Player.STATE_ENDED -> statusTextView.text = getString(R.string.status_ended)
                    }
                }
            })
        }
    }

    // 7. Called by MainActivity when a user picks a song or hits Next/Prev
    fun loadSong(songData: String, position: Int) {
        currentSongPosition = position

        // Split the string into the Name and URL
        val songName = songData.substringBefore(" - ")
        val songUrl = songData.substringAfter(" - ")

        songTitleTextView.text = songName

        if (player == null) {
            initializePlayer()
        }

        // Load the new song into ExoPlayer and play it
        val mediaItem = MediaItem.fromUri(songUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }
}