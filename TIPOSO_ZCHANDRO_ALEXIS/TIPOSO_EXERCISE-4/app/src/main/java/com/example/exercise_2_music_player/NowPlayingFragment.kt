package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class NowPlayingFragment : Fragment(R.layout.fragment_now_playing) {

    private val viewModel: MusicViewModel by activityViewModels()
    private var player: ExoPlayer? = null
    private lateinit var statusTextView: TextView
    private var songName = ""

    // The list of songs for the top window
    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusTextView = view.findViewById(R.id.songTitle)

        // --- TOP WINDOW LOGIC (The List) ---
        val listView = view.findViewById<ListView>(R.id.nowPlayingListView)
        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs)

        // When a song is clicked in the list, tell the ViewModel to play it
        listView.setOnItemClickListener { _, _, position, _ ->
            viewModel.setListAndSelect(songs, position)
        }

        // --- BOTTOM WINDOW LOGIC (The Player) ---
        viewModel.selectedSong.observe(viewLifecycleOwner) { songData ->
            songName = songData.substringBefore(" - ")
            val url = songData.substringAfter(" - ")

            statusTextView.text = "Loading: $songName"
            setupPlayer(url)
        }

        // Control Buttons
        view.findViewById<Button>(R.id.playButton).setOnClickListener { player?.play() }
        view.findViewById<Button>(R.id.pauseButton).setOnClickListener { player?.pause() }

        view.findViewById<Button>(R.id.stopButton).setOnClickListener {
            player?.stop()
            player?.seekTo(0)
            statusTextView.text = "Stopped: $songName"
        }

        view.findViewById<Button>(R.id.prevButton).setOnClickListener { viewModel.previousSong() }
        view.findViewById<Button>(R.id.nextButton).setOnClickListener { viewModel.nextSong() }
    }

    private fun setupPlayer(url: String) {
        if (player == null) {
            player = ExoPlayer.Builder(requireContext()).build()
            player?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    statusTextView.text = if (isPlaying) "Playing: $songName" else "Paused: $songName"
                }
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        viewModel.nextSong()
                    }
                }
            })
        }

        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
    }
}