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

    private var listener: MusicPlayerListener? = null
    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicPlayerListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement MusicPlayerListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_music_list, container, false)
        val listView = view.findViewById<ListView>(R.id.songsListView)
        
        val songTitles = songs.map { it.split(" - ")[0] }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songTitles)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            listener?.onSongSelected(position)
        }
        
        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    
    fun getSongs(): List<String> = songs
}
