package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment


class SongListFragment : Fragment(R.layout.fragment_song_list) {

    private var listener: SelectedSongListener? = null


    private val songs = listOf(
        "Song 1 - link",
        "Song 2 - link",
        "Song 3 - link"
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is SelectedSongListener) listener = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.songsListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs)
        listView.adapter = adapter


        listView.setOnItemClickListener { _, _, position, _ ->
            listener?.onSongSelected(position)
        }
    }
}
