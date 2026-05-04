package com.example.exercise_2_music_player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesList : ViewModel() {
    val favorites = MutableLiveData<MutableList<String>>(mutableListOf())

    fun toggleFavorite(song: String) {
        val current = favorites.value ?: mutableListOf()
        if (current.contains(song)) current.remove(song)
        else current.add(song)
        favorites.value = current
    }

    fun isFavorite(song: String): Boolean {
        return favorites.value?.contains(song) == true
    }
}