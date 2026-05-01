package com.example.exercise_2_music_player

/**
 * Interface for communication between MusicListFragment and MusicPlayerFragment
 */
interface MusicPlayerInterface {
    /**
     * Called when a song is selected from the list
     * @param songUrl The full song string (e.g., "Song 1 - https://...")
     * @param position The position of the song in the list
     */
    fun onSongSelected(songUrl: String, position: Int)

    /**
     * Called when the user wants to play the next song
     */
    fun onNextSong()

    /**
     * Called when the user wants to play the previous song
     */
    fun onPreviousSong()
}