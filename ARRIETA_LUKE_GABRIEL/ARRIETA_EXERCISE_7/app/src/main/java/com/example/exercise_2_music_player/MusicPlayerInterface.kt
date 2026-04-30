package com.example.exercise_2_music_player

interface MusicPlayerInterface {
    fun onSongSelected(position: Int)
    fun onNextClicked()
    fun onPreviousClicked()
}
