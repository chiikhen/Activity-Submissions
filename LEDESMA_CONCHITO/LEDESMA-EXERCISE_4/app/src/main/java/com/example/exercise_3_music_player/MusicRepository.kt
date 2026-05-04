package com.example.exercise_3_music_player

object MusicRepository {
    val songs = listOf(
        Song(
            title = "SoundHelix Song 1",
            url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            isFavorite = true
        ),
        Song(
            title = "SoundHelix Song 2",
            url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            isFavorite = true
        ),
        Song(
            title = "SoundHelix Song 3",
            url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            isFavorite = false
        )
    )

    var selectedSongIndex: Int = 0

    fun getSelectedSong(): Song = songs[selectedSongIndex]

    fun selectSong(index: Int) {
        if (index in songs.indices) {
            selectedSongIndex = index
        }
    }

    fun playNext() {
        selectedSongIndex = (selectedSongIndex + 1) % songs.size
    }

    fun playPrevious() {
        selectedSongIndex = if (selectedSongIndex - 1 < 0) songs.size - 1 else selectedSongIndex - 1
    }

    fun getFavoriteSongs(): List<Song> = songs.filter { it.isFavorite }
}