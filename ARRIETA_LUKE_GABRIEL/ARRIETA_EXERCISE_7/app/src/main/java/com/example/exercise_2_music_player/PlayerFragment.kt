package com.example.exercise_2_music_player

import android.content.res.ColorStateList
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

class PlayerFragment : Fragment(), PlayerSurface {
    private var listener: MusicPlayerInterface? = null

    private lateinit var songTitle: TextView
    private lateinit var songArtist: TextView
    private lateinit var songStatus: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var favoriteButton: ImageButton
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
        refreshFromActivity()
    }

    private fun initializeViews(view: View) {
        songTitle = view.findViewById(R.id.songTitle)
        songArtist = view.findViewById(R.id.songArtist)
        songStatus = view.findViewById(R.id.songStatus)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        previousButton = view.findViewById(R.id.previousButton)
        nextButton = view.findViewById(R.id.nextButton)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        seekBar = view.findViewById(R.id.seekBar)
        currentTime = view.findViewById(R.id.currentTime)
        totalTime = view.findViewById(R.id.totalTime)
        nowPlayingAlbumArt = view.findViewById(R.id.nowPlayingAlbumArt)
        blurredBackground = view.findViewById(R.id.blurredBackground)

        // Apply blur effect to background (API 31+)
        applyBlurEffect()
        updateFavoriteButton(false)
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
        view?.setOnClickListener {
            (activity as? MainActivity)?.openFullPlayer()
        }

        previousButton.setOnClickListener {
            listener?.onPreviousClicked()
        }

        nextButton.setOnClickListener {
            listener?.onNextClicked()
        }

        playPauseButton.setOnClickListener {
            togglePlayPause()
        }

        favoriteButton.setOnClickListener {
            (activity as? MainActivity)?.handleCurrentSongFavoriteClick()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentTime.text =
                        (activity as? MainActivity)?.previewSeekTime(progress) ?: "0:00"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                (activity as? MainActivity)?.beginSeekGesture()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                (activity as? MainActivity)?.completeSeekGesture(seekBar?.progress ?: 0)
            }
        })
    }

    private fun refreshFromActivity() {
        val mainActivity = activity as? MainActivity ?: return
        renderPlayerState(mainActivity.getPlayerUiState())
    }

    private fun togglePlayPause() {
        val mainActivity = activity as? MainActivity ?: return
        if (isPlaying) {
            mainActivity.pauseMusic()
        } else {
            mainActivity.playMusic()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshFromActivity()
    }

    private fun updatePlayPauseButton(playing: Boolean) {
        isPlaying = playing
        if (playing) {
            playPauseButton.setImageResource(R.drawable.ic_pause)
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play)
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val context = context ?: return
        favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
        favoriteButton.imageTintList = ColorStateList.valueOf(
            context.getColor(
                if (isFavorite) R.color.purple_light else R.color.text_secondary
            )
        )
    }

    private fun setAlbumArt(resourceId: Int) {
        nowPlayingAlbumArt.setImageResource(resourceId)
        blurredBackground.setImageResource(resourceId)
    }

    override fun renderPlayerState(state: PlayerUiState) {
        if (!::songTitle.isInitialized) {
            return
        }

        val song = state.song

        songTitle.text = song?.name ?: "Song Title"
        songArtist.text = song?.artist ?: "Artist"
        songStatus.text = state.status
        currentTime.text = state.currentTimeText
        totalTime.text = state.totalTimeText

        setAlbumArt(song?.albumArtResId ?: R.drawable.album_placeholder)
        if (!seekBar.isPressed) {
            seekBar.progress = state.progress
        }
        updatePlayPauseButton(state.isPlaying)
        updateFavoriteButton(state.isFavorite)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
