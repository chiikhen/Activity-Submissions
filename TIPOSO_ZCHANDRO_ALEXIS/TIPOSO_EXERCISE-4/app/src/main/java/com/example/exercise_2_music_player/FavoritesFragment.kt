package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    // Change these to your actual favorite songs!
    private val myFavoriteSongs = listOf(
        "1. Synesthesia - Mayonnaise",
        "2. Kabisado - IV OF SPADES",
        "3. The Way You Look At Me - Ben&Ben",
        "4. Who Knows - Daniel Caesar",
        "5. Senaryo - Adie"
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.favoritesListView)

        // Connect the list of songs to the ListView on the screen
        listView.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            myFavoriteSongs
        )
    }
}