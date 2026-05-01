package com.example.exercise_2_music_player

interface OnSongSelectedListener {
    fun onSongSelected(index: Int)
    fun onPreviousSong()
    fun onNextSong()
}