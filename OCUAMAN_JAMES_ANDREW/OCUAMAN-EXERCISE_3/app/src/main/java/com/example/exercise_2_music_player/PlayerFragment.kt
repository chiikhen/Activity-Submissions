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

    private lateinit var playPauseButton: Button
    private lateinit var previousButton: Button
    private lateinit var nextButton: Button
    private lateinit var songTitle: TextView

    private var player: ExoPlayer? = null
    private var songUrl = ""
    private var songName = ""
    private var wasPlaying = false

    private var navigator: SongNavigator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigator = context as SongNavigator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playPauseButton = view.findViewById(R.id.playPauseButton)
        previousButton = view.findViewById(R.id.previousButton)
        nextButton = view.findViewById(R.id.nextButton)
        songTitle = view.findViewById(R.id.songTitle)

        songTitle.text = "Select a song"

        playPauseButton.setOnClickListener {
            player?.let {
                if (it.isPlaying) {
                    it.pause()
                } else {
                    if (it.playbackState == Player.STATE_IDLE) {
                        it.prepare()
                    }
                    it.play()
                }
            }
        }

        previousButton.setOnClickListener {
            navigator?.onPreviousSong()
        }

        nextButton.setOnClickListener {
            navigator?.onNextSong()
        }
    }

    private fun updatePlayPauseButton(isPlaying: Boolean) {
        if (isPlaying) {
            playPauseButton.text = "⏸ Pause"
        } else {
            playPauseButton.text = "▶ Play"
        }
    }

    fun loadSong(song: String) {
        songUrl = song
        songName = songUrl.substringBefore(" - ")
        val actualUrl = songUrl.substringAfter(" - ")

        songTitle.text = songName

        player?.release()

        player = ExoPlayer.Builder(requireContext()).build().apply {
            val mediaItem = MediaItem.fromUri(actualUrl)
            setMediaItem(mediaItem)

            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlayPauseButton(isPlaying)
                    if (isPlaying) {
                        songTitle.text = "$songName - Playing"
                    } else {
                        songTitle.text = "$songName - Paused"
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> songTitle.text = "$songName - Buffering..."
                        Player.STATE_READY -> songTitle.text = "$songName - Ready"
                        Player.STATE_IDLE -> songTitle.text = "$songName - Idle"
                        Player.STATE_ENDED -> songTitle.text = "$songName - Ended"
                    }
                }
            })

            prepare()
            play()
        }
    }

    override fun onPause() {
        super.onPause()
        player?.let {
            wasPlaying = it.isPlaying
            it.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (wasPlaying) {
            player?.play()
            wasPlaying = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.stop()
        player?.release()
        player = null
    }

    override fun onDetach() {
        super.onDetach()
        navigator = null
    }
}
