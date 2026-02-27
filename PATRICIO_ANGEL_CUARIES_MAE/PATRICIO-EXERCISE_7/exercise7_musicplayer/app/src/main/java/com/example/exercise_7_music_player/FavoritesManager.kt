package com.example.exercise_7_music_player

object FavoritesManager {
    val favoriteSongs  = mutableListOf<String>()
    val favoriteRawIds = mutableListOf<Int>()

    fun add(name: String, rawId: Int) {
        if (!favoriteSongs.contains(name)) {
            favoriteSongs.add(name)
            favoriteRawIds.add(rawId)
        }
    }

    fun remove(name: String, rawId: Int) {
        val index = favoriteSongs.indexOf(name)
        if (index != -1) {
            favoriteSongs.removeAt(index)
            favoriteRawIds.removeAt(index)
        }
    }

    fun isFavorite(name: String) = favoriteSongs.contains(name)
}