package com.example.exercise_2_music_player

interface MusicPlayerListener {
    fun onSongSelected(position: Int)
    fun onNextRequested()
    fun onPreviousRequested()
}
