package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerFragment : Fragment() {
    private lateinit var player: ExoPlayer
    private lateinit var title: TextView

    interface PlayerControls {
        fun onNextSong()
        fun onPreviousSong()
    }
    private lateinit var controls: PlayerControls

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controls = context as PlayerControls
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_player, container, false)

        title = view.findViewById(R.id.songTitleFragment)

        val play = view.findViewById<Button>(R.id.playButton)
        val pause = view.findViewById<Button>(R.id.pauseButton)
        val stop = view.findViewById<Button>(R.id.stopButton)
        val next = view.findViewById<Button>(R.id.nextButton)
        val previous = view.findViewById<Button>(R.id.prevButton)

        player = ExoPlayer.Builder(requireContext()).build()

        play.setOnClickListener { player.play() }
        pause.setOnClickListener { player.pause() }
        stop.setOnClickListener {
            player.stop()
            player.clearMediaItems()
        }

        next.setOnClickListener {
            controls.onNextSong()
        }

        previous.setOnClickListener {
            controls.onPreviousSong()
        }

        return view
    }

    fun updateSong(song: String) {
        val songName = song.substringBefore(" -")
        val url = song.substringAfter(" - ")
        title.text = songName
        val mediaItem = MediaItem.fromUri(url)

        player.setMediaItem(mediaItem)
        player.prepare()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}