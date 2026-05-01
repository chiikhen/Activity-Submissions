package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class MusicListFragment : Fragment() {

    private lateinit var listener: SongControlListener
    private lateinit var songsListView: ListView

    // Attach the interface to the hosting Activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SongControlListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement SongControlListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music_list, container, false)
        songsListView = view.findViewById(R.id.songsListView)

        // Get the songs from MainActivity
        val activity = activity as MainActivity
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, activity.songs)
        songsListView.adapter = adapter

        // Handle clicks using the interface
        songsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedSong = activity.songs[position]
            listener.onSongSelected(selectedSong, position)
        }

        return view
    }
}