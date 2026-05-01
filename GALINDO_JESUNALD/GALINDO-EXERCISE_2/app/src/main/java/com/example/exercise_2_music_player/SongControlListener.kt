package com.example.exercise_2_music_player

interface SongControlListener {
    fun onSongSelected(songData: String, position: Int)
    fun onNextSong(currentPosition: Int)
    fun onPreviousSong(currentPosition: Int)
}
