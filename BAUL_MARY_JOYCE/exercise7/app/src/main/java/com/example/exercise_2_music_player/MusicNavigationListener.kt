package com.example.exercise_2_music_player

interface MusicNavigationListener {
    fun onMusicSelected(index: Int)
    fun onNextPressed()
    fun onPreviousPressed()
}