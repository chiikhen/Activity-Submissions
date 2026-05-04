package com.example.tabares_exercise_2

data class Song(
    val name: String,
    val artist: String,
    val url: String,
    val imageRes: Int
) {
    override fun toString(): String = "$name - $url"
}
