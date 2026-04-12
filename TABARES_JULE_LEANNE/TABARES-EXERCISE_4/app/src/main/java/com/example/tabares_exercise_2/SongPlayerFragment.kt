package com.example.tabares_exercise_2

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class SongPlayerFragment : Fragment() {

    private lateinit var playButton: TextView
    private lateinit var pauseButton: TextView
    private lateinit var nextButton: TextView
    private lateinit var prevButton: TextView
    private lateinit var favoriteButton: TextView
    private lateinit var songAlbumArt: ImageView
    private lateinit var songTitle: TextView
    private lateinit var songStatus: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var noSongText: View
    private lateinit var emptyBackButton: TextView
    private lateinit var playerContainer: View
    private lateinit var minimizeButton: TextView

    private lateinit var listener: OnSongInteractionListener
    private val handler = Handler(Looper.getMainLooper())
    private var isUserSeeking = false
    private var currentSong: Song? = null

    private val refreshRunnable = object : Runnable {
        override fun run() {
            renderSnapshot(listener.getPlaybackSnapshot())
            handler.postDelayed(this, 200)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSongInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_song_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        nextButton = view.findViewById(R.id.nextButton)
        prevButton = view.findViewById(R.id.prevButton)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        songAlbumArt = view.findViewById(R.id.songAlbumArt)
        songTitle = view.findViewById(R.id.songTitle)
        songStatus = view.findViewById(R.id.songStatus)
        seekBar = view.findViewById(R.id.seekBar)
        currentTimeText = view.findViewById(R.id.currentTime)
        totalTimeText = view.findViewById(R.id.totalTime)
        noSongText = view.findViewById(R.id.noSongText)
        emptyBackButton = view.findViewById(R.id.emptyBackButton)
        playerContainer = view.findViewById(R.id.playerContainer)
        minimizeButton = view.findViewById(R.id.minimizeButton)

        songTitle.isSelected = true
        setupButtons()
        renderSnapshot(listener.getPlaybackSnapshot())
    }

    override fun onStart() {
        super.onStart()
        handler.post(refreshRunnable)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun setupButtons() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                listener.onSeekTo(seekBar?.progress ?: 0)
                isUserSeeking = false
            }
        })

        playButton.setOnClickListener { listener.onTogglePlayPause() }
        pauseButton.setOnClickListener { listener.onTogglePlayPause() }
        nextButton.setOnClickListener { listener.onNextSong() }
        prevButton.setOnClickListener { listener.onPreviousSong() }
        minimizeButton.setOnClickListener { listener.onMinimizeNowPlaying() }
        emptyBackButton.setOnClickListener { listener.onMinimizeNowPlaying() }

        favoriteButton.setOnClickListener {
            currentSong?.let { song ->
                val isFavorite = listener.onToggleFavorite(song)
                updateFavoriteButton(isFavorite)
            }
        }
    }

    private fun renderSnapshot(snapshot: PlaybackSnapshot) {
        val song = snapshot.song
        if (song == null) {
            currentSong = null
            noSongText.visibility = View.VISIBLE
            playerContainer.visibility = View.GONE
            return
        }

        currentSong = song
        noSongText.visibility = View.GONE
        playerContainer.visibility = View.VISIBLE

        songTitle.text = song.name
        songStatus.text = song.artist
        songAlbumArt.setImageResource(song.imageRes)
        updateFavoriteButton(listener.isFavorite(song))

        if (!isUserSeeking) {
            val duration = snapshot.duration
            val position = snapshot.currentPosition.coerceAtLeast(0L)
            seekBar.progress = if (duration > 0) ((position * 100) / duration).toInt() else 0
            currentTimeText.text = formatTime(position)
            totalTimeText.text = formatTime(duration)
        }

        if (snapshot.isPlaying) {
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
        } else {
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        favoriteButton.text = if (isFavorite) "\u2665" else "\u2661"
        favoriteButton.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isFavorite) R.color.primary_pink else R.color.text_tertiary
            )
        )
    }

    private fun formatTime(ms: Long): String {
        if (ms <= 0L) return "00:00"
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
