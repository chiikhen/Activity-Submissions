package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listView = view.findViewById<ListView>(R.id.favoritesListView)
        refreshList(listView)

        listView.setOnItemLongClickListener { _, _, position, _ ->
            FavoritesManager.favorites.removeAt(position)
            Toast.makeText(requireContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show()
            refreshList(listView)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        view?.findViewById<ListView>(R.id.favoritesListView)?.let { refreshList(it) }
    }

    private fun refreshList(listView: ListView) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            FavoritesManager.favorites.toList()
        )
        listView.adapter = adapter
    }
}