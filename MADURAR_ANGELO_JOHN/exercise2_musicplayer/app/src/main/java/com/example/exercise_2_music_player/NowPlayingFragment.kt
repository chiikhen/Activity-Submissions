package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.material.button.MaterialButton

class NowPlayingFragment : Fragment() {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )
    private var currentIndex = 0
    private var player: ExoPlayer? = null

    private lateinit var songTitle: TextView
    private lateinit var statusText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songTitle  = view.findViewById(R.id.songTitleTextView)
        statusText = view.findViewById(R.id.statusTextView)

        val listView = view.findViewById<ListView>(R.id.songsListView)

        // Use ImageButton for play/pause/prev/next, MaterialButton for stop
        val playButton     = view.findViewById<ImageButton>(R.id.playButton)
        val pauseButton    = view.findViewById<ImageButton>(R.id.pauseButton)
        val stopButton     = view.findViewById<MaterialButton>(R.id.stopButton)
        val previousButton = view.findViewById<ImageButton>(R.id.previousButton)
        val nextButton     = view.findViewById<ImageButton>(R.id.nextButton)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            songs.map { it.substringBefore(" - ") }
        )
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            currentIndex = position
            loadSong(songs[position])
        }

        playButton.setOnClickListener {
            if (player?.playbackState == Player.STATE_IDLE) player?.prepare()
            player?.play()
        }

        pauseButton.setOnClickListener {
            player?.pause()
        }

        stopButton.setOnClickListener {
            player?.stop()
            player?.seekTo(0)
            statusText.text = "Status: Stopped"
        }

        previousButton.setOnClickListener {
            currentIndex = (currentIndex - 1 + songs.size) % songs.size
            loadSong(songs[currentIndex])
        }

        nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % songs.size
            loadSong(songs[currentIndex])
        }
    }

    private fun loadSong(songData: String) {
        songTitle.text = songData.substringBefore(" - ")
        val url = songData.substringAfter(" - ")

        player?.release()
        player = ExoPlayer.Builder(requireContext()).build().also { p ->
            p.setMediaItem(MediaItem.fromUri(url))
            p.prepare()
            p.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    statusText.text = if (isPlaying) "Status: Playing" else "Status: Paused"
                }
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> statusText.text = "Status: Buffering..."
                        Player.STATE_ENDED     -> statusText.text = "Status: Ended"
                        else -> {}
                    }
                }
            })
            p.play()
        }
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
}