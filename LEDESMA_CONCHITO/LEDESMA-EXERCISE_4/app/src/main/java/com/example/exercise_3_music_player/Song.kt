package com.example.exercise_3_music_player

data class Song(
    val title: String,
    val url: String,
    val isFavorite: Boolean = false
)