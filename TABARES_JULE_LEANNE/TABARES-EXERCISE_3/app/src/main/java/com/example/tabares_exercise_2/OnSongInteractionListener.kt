package com.example.tabares_exercise_2

interface OnSongInteractionListener {
    fun onSongSelected(song: Song, position: Int)
    fun onNextSong()
    fun onPreviousSong()
}
