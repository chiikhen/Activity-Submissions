package com.example.exercise_3_music_player

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class NowPlayingFragment : Fragment() {

    private var player: ExoPlayer? = null
    private lateinit var songTitleText: TextView
    private lateinit var songsListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songTitleText = view.findViewById(R.id.songTitle)
        songsListView = view.findViewById(R.id.songsListView)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            MusicRepository.songs.map { it.title }
        )
        songsListView.adapter = adapter

        songsListView.setOnItemClickListener { _, _, position, _ ->
            MusicRepository.selectSong(position)
            playSelectedSong(autoPlay = true)
        }

        view.findViewById<Button>(R.id.playButton).setOnClickListener {
            if (player == null) {
                initializePlayer()
            }
            if (player?.currentMediaItem == null) {
                playSelectedSong(autoPlay = true)
            } else {
                if (player?.playbackState == Player.STATE_IDLE) {
                    player?.prepare()
                }
                player?.play()
            }
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
            MusicRepository.playPrevious()
            playSelectedSong(autoPlay = true)
        }

        view.findViewById<Button>(R.id.btnNext).setOnClickListener {
            MusicRepository.playNext()
            playSelectedSong(autoPlay = true)
        }

        updateSelectedSongText("Ready")
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
        updateSelectedSongText(if (player?.isPlaying == true) "Playing" else "Ready")
    }

    private fun initializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build()
            setupPlayerListener()
        }
    }

    private fun playSelectedSong(autoPlay: Boolean) {
        initializePlayer()
        val song = MusicRepository.getSelectedSong()
        songTitleText.text = song.title
        val mediaItem = MediaItem.fromUri(song.url)
        player?.apply {
            stop()
            clearMediaItems()
            setMediaItem(mediaItem)
            prepare()
            if (autoPlay) {
                play()
            }
        }
    }

    private fun setupPlayerListener() {
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (player?.playbackState != Player.STATE_IDLE) {
                    updateStatus(if (isPlaying) "Playing" else "Paused")
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_IDLE -> updateStatus("Stopped")
                    Player.STATE_BUFFERING -> updateStatus("Buffering")
                    Player.STATE_READY -> {
                        if (player?.playWhenReady == true) updateStatus("Playing")
                        else updateStatus("Ready")
                    }
                    Player.STATE_ENDED -> updateStatus("Ended")
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateSelectedSongText(status: String) {
        val song = MusicRepository.getSelectedSong()
        songTitleText.text = "${song.title} - $status"
    }

    private fun updateStatus(status: String) {
        updateSelectedSongText(status)
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}