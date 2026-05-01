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

    // Interface for communication between fragment and activity
    interface OnSongSelectedListener {
        fun onSongSelected(songData: String, position: Int)
    }

    private var listener: OnSongSelectedListener? = null

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )


    fun getSongs(): List<String> = songs

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSongSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.music_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.songsListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            listener?.onSongSelected(songs[position], position)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}