package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    // Just a placeholder list for your favorite songs
    private val favSongs = listOf("Favorite Song A", "Favorite Song B")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView = view.findViewById<ListView>(R.id.favoritesListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, favSongs)
        listView?.adapter = adapter
    }
}