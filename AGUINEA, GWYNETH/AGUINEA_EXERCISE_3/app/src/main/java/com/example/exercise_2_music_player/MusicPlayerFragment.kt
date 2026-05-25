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

class MusicPlayerFragment : Fragment() {

    private var player: ExoPlayer? = null
    private lateinit var songTitleTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var prevButton: Button
    private lateinit var nextButton: Button

    private var currentSongUrl: String? = null
    private var listener: MusicPlayerListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicPlayerListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music_player, container, false)
        songTitleTextView = view.findViewById(R.id.songTitle)
        statusTextView = view.findViewById(R.id.statusText)
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        prevButton = view.findViewById(R.id.prevButton)
        nextButton = view.findViewById(R.id.nextButton)

        playButton.setOnClickListener { player?.play() }
        pauseButton.setOnClickListener { player?.pause() }
        stopButton.setOnClickListener {
            player?.stop()
            player?.seekTo(0)
        }
        prevButton.setOnClickListener { listener?.onPreviousSong() }
        nextButton.setOnClickListener { listener?.onNextSong() }

        return view
    }

    fun playSong(songData: String) {
        val title = songData.substringBefore(" - ")
        currentSongUrl = songData.substringAfter(" - ")
        songTitleTextView.text = title
        
        initializePlayer()
    }

    private fun initializePlayer() {
        val url = currentSongUrl ?: return
        
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
                exoPlayer.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        statusTextView.text = if (isPlaying) "Status: Playing" else "Status: Paused"
                    }
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_BUFFERING -> statusTextView.text = "Status: Buffering..."
                            Player.STATE_READY -> statusTextView.text = "Status: Ready"
                            Player.STATE_IDLE -> statusTextView.text = "Status: Stopped"
                            Player.STATE_ENDED -> statusTextView.text = "Status: Finished"
                        }
                    }
                })
            }
        }

        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}