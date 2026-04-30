package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class MyMusicFragment : Fragment() {
    private lateinit var songsListView: ListView

    private val songs = listOf(
        "Song 1",
        "Song 2",
        "Song 3"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songsListView = view.findViewById(R.id.songsListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs)
        songsListView.adapter = adapter

        songsListView.setOnItemClickListener { _, _, position, _ ->
            val songData = songs[position]
            // Handle song selection
        }
    }
}