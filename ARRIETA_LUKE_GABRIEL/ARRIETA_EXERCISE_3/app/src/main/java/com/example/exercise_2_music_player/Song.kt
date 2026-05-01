package com.example.exercise_2_music_player

data class Song(
    val name: String,
    val artist: String,
    val url: String,
    val albumArtResId: Int = R.drawable.album_placeholder  // Default placeholder
) {
    companion object {
        fun fromString(songData: String): Song {
            val parts = songData.split(" - ")
            val name = parts.getOrNull(0) ?: "Unknown"
            val url = parts.getOrNull(1) ?: ""
            return Song(name, "Unknown Artist", url)
        }
    }

    override fun toString(): String {
        return "$name - $url"
    }
}
