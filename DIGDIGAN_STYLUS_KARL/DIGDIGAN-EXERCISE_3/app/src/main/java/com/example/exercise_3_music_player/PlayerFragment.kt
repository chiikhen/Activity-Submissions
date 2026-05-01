package com.example.exercise_3_music_player

import android.annotation.SuppressLint
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

    private lateinit var songTitleText: TextView
    private var player: ExoPlayer? = null
    private var currentTitle: String = "Select a song"
    private var currentUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songTitleText = view.findViewById(R.id.songTitle)
        songTitleText.text = currentTitle

        view.findViewById<Button>(R.id.playButton).setOnClickListener {
            player?.play()
        }

        view.findViewById<Button>(R.id.pauseButton).setOnClickListener {
            player?.pause()
        }

        view.findViewById<Button>(R.id.stopButton).setOnClickListener {
            player?.stop()
            player?.seekTo(0)
            updateStatus("Stopped")
        }

        view.findViewById<Button>(R.id.btnPrevious).setOnClickListener {
            (parentFragmentManager.findFragmentById(R.id.list_container) as? MusicListFragment)?.playPrevious()
        }

        view.findViewById<Button>(R.id.btnNext).setOnClickListener {
            (parentFragmentManager.findFragmentById(R.id.list_container) as? MusicListFragment)?.playNext()
        }
    }

    override fun onStart() {
        super.onStart()
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build()
        }

        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                val status = when (state) {
                    Player.STATE_ENDED -> "Ended"
                    Player.STATE_READY -> if (player!!.playWhenReady) "Playing" else "Paused"
                    Player.STATE_BUFFERING -> "Buffering"
                    Player.STATE_IDLE -> "Idle"
                    else -> ""
                }
                if (status.isNotEmpty()) {
                    updateStatus(status)
                }
            }
        })


        currentUrl?.let { url ->
            val mediaItem = MediaItem.fromUri(url)
            player?.setMediaItem(mediaItem)
            player?.prepare()
        }
    }

    fun playNewSong(fullData: String) {
        currentTitle = fullData.substringBefore(" - ")
        currentUrl = fullData.substringAfter(" - ")
        songTitleText.text = currentTitle

        player?.stop()
        if (currentUrl != null) {
            val mediaItem = MediaItem.fromUri(currentUrl!!)
            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.play()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateStatus(status: String) {
        songTitleText.text = "$currentTitle - $status"
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
