package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class SongListFragment : Fragment() {
    private lateinit var listener: OnSongSelectedListener

    interface OnSongSelectedListener {
        fun onSongSelected(song: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnSongSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song_list, container, false)
        val listView = view.findViewById<ListView>(R.id.songsListFragment)
        val songs = listOf(
            "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            songs.map { it.substringBefore(" -") }
        )
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            listener.onSongSelected(songs[position])
        }

        return view
    }
}