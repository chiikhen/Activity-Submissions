package com.example.exercise_2_music_player

interface MusicPlayerInterface {
    fun onSongSelected(songData: String)
    fun onNextRequested()
    fun onPreviousRequested()
}
