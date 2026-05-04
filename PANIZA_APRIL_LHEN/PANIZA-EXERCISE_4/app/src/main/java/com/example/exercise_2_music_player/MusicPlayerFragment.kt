package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MusicPlayerFragment : Fragment() {

    // UI elements
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var songTitleTextView: TextView
    private lateinit var songStatusTextView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView

    private var musicPlayerInterface: MusicPlayerInterface? = null
    private var currentSongIndex: Int = -1
    private var songList: List<String> = emptyList()

    private var wasPlaying: Boolean = false

    // ExoPlayer setup
    private var player: ExoPlayer? = null

    // Handler for updating seek bar
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            player?.let {
                if (it.duration > 0) {
                    val progress = ((it.currentPosition * 100) / it.duration).toInt()
                    seekBar.progress = progress
                    currentTimeTextView.text = formatTime(it.currentPosition)
                    totalTimeTextView.text = formatTime(it.duration)
                }
            }
            handler.postDelayed(this, 1000)
        }
    }

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
        return inflater.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI(view)
        setupButtonListeners()

        musicPlayerInterface?.let {
            songList = it.getSongList()
            currentSongIndex = it.getCurrentSongIndex()
            if (currentSongIndex >= 0) {
                loadSongOnly(currentSongIndex)  // Load without playing for initial setup
            }
        }
    }

    private fun initializeUI(view: View) {
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        previousButton = view.findViewById(R.id.previousButton)
        nextButton = view.findViewById(R.id.nextButton)
        songTitleTextView = view.findViewById(R.id.songTitle)
        songStatusTextView = view.findViewById(R.id.songStatus)
        seekBar = view.findViewById(R.id.seekBar)
        currentTimeTextView = view.findViewById(R.id.currentTime)
        totalTimeTextView = view.findViewById(R.id.totalTime)
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
            player?.stop()
            player?.seekTo(0)
            seekBar.progress = 0
            currentTimeTextView.text = getString(R.string.time_zero)
            wasPlaying = false
        }

        previousButton.setOnClickListener {
            musicPlayerInterface?.onPreviousSong()
        }

        nextButton.setOnClickListener {
            musicPlayerInterface?.onNextSong()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player?.let {
                        val newPosition = (it.duration * progress) / 100
                        it.seekTo(newPosition)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun loadAndPlaySong(songIndex: Int) {
        if (songIndex >= 0 && songIndex < songList.size) {
            currentSongIndex = songIndex
            val songData = songList[songIndex]
            val songName = songData.substringBefore("|").trim()
            val songUrl = songData.substringAfter(" - ").trim()

            songTitleTextView.text = songName
            songStatusTextView.text = getString(R.string.status_buffering)

            // Update previous/next button states
            previousButton.isEnabled = songIndex > 0
            nextButton.isEnabled = songIndex < songList.size - 1

            // Mark that the user has intentionally started playback
            wasPlaying = true

            // Initialize player with new song and start playing
            initializePlayer(songUrl)
        }
    }

    private fun loadSongOnly(songIndex: Int) {
        if (songIndex >= 0 && songIndex < songList.size) {
            currentSongIndex = songIndex
            val songData = songList[songIndex]
            val songName = songData.substringBefore("|").trim()
            val songUrl = songData.substringAfter(" - ").trim()

            songTitleTextView.text = songName
            songStatusTextView.text = getString(R.string.status_ready)

            // Update previous/next button states
            previousButton.isEnabled = songIndex > 0
            nextButton.isEnabled = songIndex < songList.size - 1

            // Initialize player with new song but DON'T auto-play
            initializePlayerOnly(songUrl)
        }
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format(java.util.Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    override fun onStart() {
        super.onStart()
        handler.post(updateSeekBarRunnable)
    }

    private fun initializePlayer(songUrl: String) {
        // Release existing player
        player?.release()

        // Setup the ExoPlayer
        player = ExoPlayer.Builder(requireContext()).build()

        // Put the song URL to the media Item
        val mediaItem = MediaItem.fromUri(songUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()

        // Add listener for updating the status text view based on player's state
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songStatusTextView.text = getString(R.string.status_playing)
                } else {
                    if (player?.playbackState == Player.STATE_READY) {
                        songStatusTextView.text = getString(R.string.status_paused)
                    }
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> {
                        songStatusTextView.text = getString(R.string.status_buffering)
                    }
                    Player.STATE_READY -> {
                        if (player?.isPlaying == true) {
                            songStatusTextView.text = getString(R.string.status_playing)
                        } else {
                            songStatusTextView.text = getString(R.string.status_ready_play)
                            // Auto-play when ready (for song selection)
                            player?.play()
                        }
                    }
                    Player.STATE_IDLE -> {
                        songStatusTextView.text = getString(R.string.status_stopped)
                    }
                    Player.STATE_ENDED -> {
                        songStatusTextView.text = getString(R.string.status_finished)
                        // Auto play next song when current song ends
                        musicPlayerInterface?.onNextSong()
                    }
                }
            }
        })
    }

    private fun initializePlayerOnly(songUrl: String) {
        // Release existing player
        player?.release()

        // Setup the ExoPlayer
        player = ExoPlayer.Builder(requireContext()).build()

        // Put the song URL to the media Item
        val mediaItem = MediaItem.fromUri(songUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()

        // Note: Don't auto-play, just prepare the player with the new song
    }

    override fun onResume() {
        super.onResume()
        // Only resume if the user had actively started playback before
        if (wasPlaying) {
            player?.play()
        }
    }

    override fun onPause() {
        super.onPause()
        wasPlaying = player?.isPlaying == true
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateSeekBarRunnable)
        player?.release()
        player = null
    }

    override fun onDetach() {
        super.onDetach()
        musicPlayerInterface = null
    }

    @Suppress("unused")
    fun updateSongList(newSongList: List<String>) {
        songList = newSongList
        // Update button states if needed
        if (currentSongIndex >= 0) {
            previousButton.isEnabled = currentSongIndex > 0
            nextButton.isEnabled = currentSongIndex < songList.size - 1
        }
    }
}
