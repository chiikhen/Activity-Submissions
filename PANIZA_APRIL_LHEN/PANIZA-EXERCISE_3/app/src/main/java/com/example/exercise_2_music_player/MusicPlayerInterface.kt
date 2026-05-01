package com.example.exercise_2_music_player

interface MusicPlayerInterface {
    /**
     * Called when a song is selected from the list
     */
    fun onSongSelected(songIndex: Int)

    /**
     * Called when the user wants to play the previous song
     */
    fun onPreviousSong()

    /**
     * Called when the user wants to play the next song
     */
    fun onNextSong()

    /**
     * Get the current song list
     */
    fun getSongList(): List<String>

    /**
     * Get the current song index
     */
    fun getCurrentSongIndex(): Int
}
