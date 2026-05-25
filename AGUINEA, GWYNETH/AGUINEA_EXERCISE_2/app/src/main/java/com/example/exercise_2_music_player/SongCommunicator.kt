package com.example.exercise_2_music_player

interface SongCommunicator {
    fun onSongSelected(songData: String, position: Int)
    fun onNextRequested()
    fun onPreviousRequested()
}