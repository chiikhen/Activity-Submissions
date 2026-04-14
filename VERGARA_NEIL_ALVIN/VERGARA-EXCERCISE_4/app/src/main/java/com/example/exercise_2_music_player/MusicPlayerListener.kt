package com.example.exercise_2_music_player

interface MusicPlayerListener {
    fun onSongSelected(songTitle: String, songUrl: String)
    fun onPreviousSong()
    fun onNextSong()
}