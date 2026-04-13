package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment() {

    private var listener: MusicListFragment.OnSongSelectedListener? = null

    private val favorites = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicListFragment.OnSongSelectedListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorites_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.favoritesListView)
        val songNames = favorites.map { it.substringBefore(" - ") }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songNames)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            listener?.onSongSelected(favorites[position], position)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}