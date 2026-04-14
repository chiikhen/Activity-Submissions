package com.example.exercise_2_music_player

data class Song(
    val title: String,
    val artist: String,
    val url: String,
    val duration: String,
    var isFavorite: Boolean = false
)