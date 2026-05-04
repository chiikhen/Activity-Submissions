package com.example.exercise_2_music_player

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class FullPlayerFragment : Fragment(), PlayerSurface {
    private lateinit var closeButton: TextView
    private lateinit var statusText: TextView
    private lateinit var titleText: TextView
    private lateinit var artistText: TextView
    private lateinit var favoriteButton: ImageButton
    private lateinit var playPauseButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var volumeSeekBar: SeekBar
    private lateinit var volumeValueText: TextView
    private lateinit var albumArtImage: ImageView
    private lateinit var blurredBackground: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_full_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupClickListeners()
        refreshFromActivity()
    }

    private fun initializeViews(view: View) {
        closeButton = view.findViewById(R.id.closePlayerText)
        statusText = view.findViewById(R.id.fullPlayerStatus)
        titleText = view.findViewById(R.id.fullPlayerSongTitle)
        artistText = view.findViewById(R.id.fullPlayerSongArtist)
        favoriteButton = view.findViewById(R.id.fullPlayerFavoriteButton)
        playPauseButton = view.findViewById(R.id.fullPlayerPlayPauseButton)
        previousButton = view.findViewById(R.id.fullPlayerPreviousButton)
        nextButton = view.findViewById(R.id.fullPlayerNextButton)
        seekBar = view.findViewById(R.id.fullPlayerSeekBar)
        currentTimeText = view.findViewById(R.id.fullPlayerCurrentTime)
        totalTimeText = view.findViewById(R.id.fullPlayerTotalTime)
        volumeSeekBar = view.findViewById(R.id.volumeSeekBar)
        volumeValueText = view.findViewById(R.id.volumeValueText)
        albumArtImage = view.findViewById(R.id.fullPlayerAlbumArt)
        blurredBackground = view.findViewById(R.id.fullPlayerBackground)
    }

    private fun setupClickListeners() {
        closeButton.setOnClickListener {
            (activity as? MainActivity)?.closeFullPlayer()
        }

        previousButton.setOnClickListener {
            (activity as? MainActivity)?.onPreviousClicked()
        }

        nextButton.setOnClickListener {
            (activity as? MainActivity)?.onNextClicked()
        }

        playPauseButton.setOnClickListener {
            val mainActivity = activity as? MainActivity ?: return@setOnClickListener
            if (mainActivity.isCurrentSongPlaying()) {
                mainActivity.pauseMusic()
            } else {
                mainActivity.playMusic()
            }
        }

        favoriteButton.setOnClickListener {
            (activity as? MainActivity)?.handleCurrentSongFavoriteClick()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentTimeText.text =
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

        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    (activity as? MainActivity)?.setPlayerVolume(progress)
                    volumeValueText.text = "$progress%"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    private fun refreshFromActivity() {
        val mainActivity = activity as? MainActivity ?: return
        renderPlayerState(mainActivity.getPlayerUiState())
    }

    override fun onResume() {
        super.onResume()
        refreshFromActivity()
    }

    override fun renderPlayerState(state: PlayerUiState) {
        if (!::titleText.isInitialized) {
            return
        }

        val context = context ?: return
        val song = state.song

        titleText.text = song?.name ?: "Pick a song to start"
        artistText.text = song?.artist ?: "Your playlist is ready"
        statusText.text = state.status
        currentTimeText.text = state.currentTimeText
        totalTimeText.text = state.totalTimeText
        volumeValueText.text = "${state.volumeProgress}%"

        albumArtImage.setImageResource(song?.albumArtResId ?: R.drawable.album_placeholder)
        blurredBackground.setImageResource(song?.albumArtResId ?: R.drawable.album_placeholder)

        if (!seekBar.isPressed) {
            seekBar.progress = state.progress
        }
        if (!volumeSeekBar.isPressed) {
            volumeSeekBar.progress = state.volumeProgress
        }

        playPauseButton.setImageResource(
            if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
        favoriteButton.setImageResource(
            if (state.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
        favoriteButton.imageTintList = ColorStateList.valueOf(
            context.getColor(
                if (state.isFavorite) R.color.purple_light else R.color.text_secondary
            )
        )
    }
}
