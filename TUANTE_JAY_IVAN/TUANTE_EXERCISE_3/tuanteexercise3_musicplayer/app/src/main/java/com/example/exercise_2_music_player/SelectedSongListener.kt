package com.example.exercise_2_music_player

interface SelectedSongListener {
    fun onSongSelected(index: Int)
    fun onNextSong()
    fun onPreviousSong()
}