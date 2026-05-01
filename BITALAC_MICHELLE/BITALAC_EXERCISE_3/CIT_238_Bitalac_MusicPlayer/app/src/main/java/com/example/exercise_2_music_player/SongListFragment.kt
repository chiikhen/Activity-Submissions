package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class SongListFragment(private val songs: List<String>) : Fragment() {

    interface OnSongSelectedListener {
        fun onSongSelected(position: Int)
        fun onNextRequested()
        fun onPreviousRequested()
    }

    private lateinit var listener: OnSongSelectedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnSongSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_song_list, container, false)

        val listView = view.findViewById<ListView>(R.id.songsListView)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            songs
        )
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            listener.onSongSelected(position)
        }

        return view
    }
}