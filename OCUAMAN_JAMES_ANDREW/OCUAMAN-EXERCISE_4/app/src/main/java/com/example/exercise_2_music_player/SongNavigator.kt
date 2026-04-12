package com.example.exercise_2_music_player

interface SongNavigator {
    fun onSongSelected(position: Int)
    fun onPreviousSong()
    fun onNextSong()
}
