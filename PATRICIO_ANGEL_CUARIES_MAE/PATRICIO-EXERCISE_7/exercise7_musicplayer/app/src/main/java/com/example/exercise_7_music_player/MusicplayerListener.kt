package com.example.exercise_7_music_player

interface MusicplayerListener {
    fun onSongSelected(songTitle: String, songUrl: String, position: Int)
    fun onPreviousSong()
    fun onNextSong()
}