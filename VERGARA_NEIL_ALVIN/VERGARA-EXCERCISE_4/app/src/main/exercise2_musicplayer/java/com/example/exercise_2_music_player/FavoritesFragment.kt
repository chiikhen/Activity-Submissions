package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment() {
    private lateinit var favoritesListView: ListView

    private val favorites = listOf(
        "❤️ Song 1 - Favorite",
        "❤️ Song 2 - Favorite",
        "❤️ Song 3 - Favorite"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesListView = view.findViewById(R.id.favoritesListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, favorites)
        favoritesListView.adapter = adapter

        favoritesListView.setOnItemClickListener { _, _, position, _ ->
            // Handle favorite song selection
        }
    }
}