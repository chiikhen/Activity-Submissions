package com.example.exercise_3_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class MusicListFragment : Fragment() {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "Song 4 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 5 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 6 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "Song 7 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 8 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 9 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )
    private var currentPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.songsListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            currentPosition = position
            updatePlayer()
        }
    }

    fun playNext() {
        currentPosition = (currentPosition + 1) % songs.size
        updatePlayer()
    }

    fun playPrevious() {
        currentPosition = if (currentPosition - 1 < 0) songs.size - 1 else currentPosition - 1
        updatePlayer()
    }

    private fun updatePlayer() {
        val playerFrag = parentFragmentManager.findFragmentById(R.id.player_container) as? PlayerFragment
        playerFrag?.playNewSong(songs[currentPosition])
    }
}