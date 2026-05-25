package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment() {

    private val favoriteSongs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        val favoritesListView: ListView = view.findViewById(R.id.favoritesListView)
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, favoriteSongs)
        favoritesListView.adapter = adapter

        return view
    }
}