package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class FavoritesFragment : Fragment(),
    FavoriteListFragment.OnSongSelectedListener,
    PlayerFragment.PlayerControls {

    private val favoritesViewModel: FavoritesList by activityViewModels()
    private var currentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    private fun getPlayerFragment() =
        childFragmentManager.findFragmentById(R.id.favPlayerFragment) as? PlayerFragment

    private fun getFavorites() = favoritesViewModel.favorites.value ?: mutableListOf()

    override fun onSongSelected(song: String) {
        currentIndex = getFavorites().indexOf(song)
        getPlayerFragment()?.updateSong(song)
    }

    override fun onNextSong() {
        val favorites = getFavorites()
        if (favorites.isEmpty()) return
        currentIndex = (currentIndex + 1) % favorites.size
        getPlayerFragment()?.updateSong(favorites[currentIndex])
    }

    override fun onPreviousSong() {
        val favorites = getFavorites()
        if (favorites.isEmpty()) return
        currentIndex = if (currentIndex - 1 < 0) favorites.size - 1 else currentIndex - 1
        getPlayerFragment()?.updateSong(favorites[currentIndex])
    }
}