package com.example.exercise_2_music_player

import android.content.Context
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class PlayerFragment : Fragment() {
    private var listener: MusicPlayerInterface? = null

    private lateinit var songTitle: TextView
    private lateinit var songArtist: TextView
    private lateinit var songStatus: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var totalTime: TextView
    private lateinit var nowPlayingAlbumArt: ImageView
    private lateinit var blurredBackground: ImageView

    private var isPlaying = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicPlayerInterface) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        songTitle = view.findViewById(R.id.songTitle)
        songArtist = view.findViewById(R.id.songArtist)
        songStatus = view.findViewById(R.id.songStatus)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        previousButton = view.findViewById(R.id.previousButton)
        nextButton = view.findViewById(R.id.nextButton)
        seekBar = view.findViewById(R.id.seekBar)
        currentTime = view.findViewById(R.id.currentTime)
        totalTime = view.findViewById(R.id.totalTime)
        nowPlayingAlbumArt = view.findViewById(R.id.nowPlayingAlbumArt)
        blurredBackground = view.findViewById(R.id.blurredBackground)

        // Apply blur effect to background (API 31+)
        applyBlurEffect()
    }

    private fun applyBlurEffect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val blurEffect = RenderEffect.createBlurEffect(
                25f, // radiusX
                25f, // radiusY
                Shader.TileMode.CLAMP
            )
            blurredBackground.setRenderEffect(blurEffect)
        }
        // For older APIs, the alpha transparency will provide a similar softening effect
        blurredBackground.alpha = 0.5f
    }

    private fun setupClickListeners() {
        previousButton.setOnClickListener {
            listener?.onPreviousClicked()
        }

        nextButton.setOnClickListener {
            listener?.onNextClicked()
        }

        playPauseButton.setOnClickListener {
            togglePlayPause()
        }
    }

    private fun togglePlayPause() {
        val mainActivity = (activity as? MainActivity)
        if (isPlaying) {
            mainActivity?.pauseMusic()
            updatePlayPauseButton(false)
        } else {
            mainActivity?.playMusic()
            updatePlayPauseButton(true)
        }
    }

    fun updateSongInfo(song: Song) {
        songTitle.text = song.name
        songArtist.text = song.artist
        // Album art will be updated separately when you add images
    }

    fun updateStatus(status: String) {
        songStatus.text = status

        // Update play/pause button based on status
        when (status) {
            "Playing" -> updatePlayPauseButton(true)
            "Paused", "Ready", "Idle" -> updatePlayPauseButton(false)
        }
    }

    fun updatePlayPauseButton(playing: Boolean) {
        isPlaying = playing
        if (playing) {
            playPauseButton.setImageResource(R.drawable.ic_pause)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play)
        }
    }

    fun setAlbumArt(resourceId: Int) {
        nowPlayingAlbumArt.setImageResource(resourceId)
        blurredBackground.setImageResource(resourceId)
    }

    fun getSeekBar(): SeekBar = seekBar

    fun getCurrentTimeTextView(): TextView = currentTime

    fun getTotalTimeTextView(): TextView = totalTime

    fun getAlbumArtImageView(): ImageView = nowPlayingAlbumArt

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
