package com.example.exercise_7_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MyMusicFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var nowPlayingTitle: TextView
    private lateinit var btnPlay: Button
    private lateinit var btnPause: Button
    private lateinit var btnStop: Button
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button

    private var player: ExoPlayer? = null
    private var currentIndex = 0

    companion object {
        val songNames = listOf(
            "Fly Me to the Moon (Cover) - The Macarons Project",
            "Sun - Derik Fein",
            "The Alternative - Lyn Lapid",
            "The Moon Will Sing - The Crane Wives",
            "Quiet Rebellion - Kelly Boesch"
        )

        val songRawIds = listOf(
            R.raw.song1,
            R.raw.song2,
            R.raw.song3,
            R.raw.song4,
            R.raw.song5
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_my_music, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView        = view.findViewById(R.id.myMusicListView)
        nowPlayingTitle = view.findViewById(R.id.nowPlayingTitle)
        btnPlay         = view.findViewById(R.id.btnPlay)
        btnPause        = view.findViewById(R.id.btnPause)
        btnStop         = view.findViewById(R.id.btnStop)
        btnPrevious     = view.findViewById(R.id.btnPrevious)
        btnNext         = view.findViewById(R.id.btnNext)

        val adapter = SongAdapter(requireContext(), songNames, songRawIds)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            currentIndex = position
            loadSong(currentIndex)
        }

        btnPlay.setOnClickListener {
            if (player?.playbackState == Player.STATE_IDLE) player?.prepare()
            player?.play()
        }
        btnPause.setOnClickListener { player?.pause() }
        btnStop.setOnClickListener {
            player?.stop()
            player?.seekTo(0)
            nowPlayingTitle.text = "${songNames[currentIndex]} - Stopped"
        }
        btnPrevious.setOnClickListener {
            currentIndex = (currentIndex - 1 + songNames.size) % songNames.size
            loadSong(currentIndex)
        }
        btnNext.setOnClickListener {
            currentIndex = (currentIndex + 1) % songNames.size
            loadSong(currentIndex)
        }
    }

    private fun loadSong(index: Int) {
        player?.release()

        val uri = android.net.Uri.parse(
            "android.resource://${requireContext().packageName}/${songRawIds[index]}"
        )

        player = ExoPlayer.Builder(requireContext()).build().also { exo ->
            exo.setMediaItem(MediaItem.fromUri(uri))
            exo.prepare()
            exo.play()

            exo.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    nowPlayingTitle.text = if (isPlaying)
                        "${songNames[index]} - Playing"
                    else
                        "${songNames[index]} - Paused"
                }

                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED)
                        nowPlayingTitle.text = "${songNames[index]} - Done"
                }
            })
        }

        nowPlayingTitle.text = "${songNames[index]} - Loading…"
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}