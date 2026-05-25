package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class SongListFragment : Fragment() {

    private var communicator: SongCommunicator? = null
    
    private val songs = listOf(
        "Blinding Lights - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Stay - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Heat Waves - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "Starboy - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SongCommunicator) {
            communicator = context
        } else {
            throw RuntimeException("$context must implement SongCommunicator")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song_list, container, false)
        val listView: ListView = view.findViewById(R.id.songsListView)
        
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item_song, songs)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            communicator?.onSongSelected(songs[position], position)
        }
        
        return view
    }

    override fun onDetach() {
        super.onDetach()
        communicator = null
    }
}