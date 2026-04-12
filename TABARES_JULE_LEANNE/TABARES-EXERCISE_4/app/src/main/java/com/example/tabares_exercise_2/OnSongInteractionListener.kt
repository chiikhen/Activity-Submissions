package com.example.tabares_exercise_2

interface OnSongInteractionListener {
    fun onSongSelected(song: Song, position: Int)
    fun onNextSong()
    fun onPreviousSong()
    fun onTogglePlayPause()
    fun onToggleFavorite(song: Song): Boolean
    fun onSeekTo(progress: Int)
    fun onMinimizeNowPlaying()
    fun isFavorite(song: Song): Boolean
    fun getFavoriteSongs(): List<Song>
    fun getPlaybackSnapshot(): PlaybackSnapshot
}
