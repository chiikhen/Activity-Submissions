package com.example.exercise_3_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favoriteSongs = MusicRepository.getFavoriteSongs()
        val favoritesListView = view.findViewById<ListView>(R.id.favoritesListView)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            favoriteSongs.map { it.title }
        )
        favoritesListView.adapter = adapter

        favoritesListView.setOnItemClickListener { _, _, position, _ ->
            val selectedFavorite = favoriteSongs[position]
            val originalIndex = MusicRepository.songs.indexOfFirst { it.title == selectedFavorite.title }
            MusicRepository.selectSong(originalIndex)
            findNavController().navigate(R.id.nav_now_playing)
        }
    }
}