package com.example.exercise_2_music_player

interface MusicPlayerListener {
    fun onSongSelected(song: String, index: Int)
    fun onNextSong()
    fun onPreviousSong()
}