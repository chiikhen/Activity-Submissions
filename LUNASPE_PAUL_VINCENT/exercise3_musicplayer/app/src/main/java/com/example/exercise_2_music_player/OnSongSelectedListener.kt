package com.example.exercise_2_music_player

interface OnSongSelectedListener {
    fun onSongSelected(songData: String)
    fun onPreviousSong()
    fun onNextSong()
}
