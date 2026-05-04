package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class MusicListFragment : Fragment(R.layout.fragment_music_list) {
    // Sharing the same ViewModel instance as PlayerFragment
    private val viewModel: MusicViewModel by activityViewModels()

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView = view.findViewById<ListView>(R.id.songsListView)
        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs)

        listView.setOnItemClickListener { _, _, position, _ ->
            viewModel.setListAndSelect(songs, position)
        }
    }
}