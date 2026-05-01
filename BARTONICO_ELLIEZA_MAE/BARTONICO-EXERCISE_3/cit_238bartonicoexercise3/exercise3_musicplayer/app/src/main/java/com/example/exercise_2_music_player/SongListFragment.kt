package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

// Requirement #1: Fragment for the music list
class SongListFragment : Fragment(R.layout.fragment_song_list) {

    private var listener: SelectedSongListener? = null

    // Song data for the list
    private val songs = listOf(
        "Clarity - Zedd",
        "Konsensya - IV OF SPADES",
        "Aura - IV OF SPADES"
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Attach the interface to the Activity
        if (context is SelectedSongListener) listener = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.songsListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs)
        listView.adapter = adapter

        // Requirement #3: Tell Activity a song was clicked
        listView.setOnItemClickListener { _, _, position, _ ->
            listener?.onSongSelected(position)
        }
    }
}
