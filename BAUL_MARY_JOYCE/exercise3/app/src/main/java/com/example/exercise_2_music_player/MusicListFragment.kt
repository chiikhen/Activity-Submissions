package com.example.exercise_2_music_player

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class MusicListFragment : Fragment() {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    private var listener: MusicNavigationListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicNavigationListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_music_list, container, false)
        val songsListView = view.findViewById<ListView>(R.id.songsListView)

        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            songs
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val itemView = super.getView(position, convertView, parent)
                val text = itemView.findViewById<TextView>(android.R.id.text1)

                // Set the text color to white here
                text.setTextColor(Color.WHITE)

                return itemView
            }
        }

        songsListView.adapter = adapter

        songsListView.setOnItemClickListener { _, _, position, _ ->
            listener?.onMusicSelected(position)
        }

        return view
    }
}