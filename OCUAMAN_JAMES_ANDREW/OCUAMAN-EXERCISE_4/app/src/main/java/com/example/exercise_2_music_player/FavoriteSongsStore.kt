package com.example.exercise_2_music_player

object FavoriteSongsStore {
    private val favoriteSongs = linkedSetOf<String>()

    fun toggle(song: String): Boolean {
        return if (favoriteSongs.contains(song)) {
            favoriteSongs.remove(song)
            false
        } else {
            favoriteSongs.add(song)
            true
        }
    }

    fun getAll(): List<String> = favoriteSongs.toList()

    fun isFavorite(song: String): Boolean = favoriteSongs.contains(song)
}
