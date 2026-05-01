package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SongListFragment.OnSongSelectedListener, PlayerFragment.PlayerControls {
    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
    }

    override fun onSongSelected(song: String) {
        currentIndex = songs.indexOf(song)
        updatePlayer()
    }

    private fun updatePlayer() {
        val playerFragment =
            supportFragmentManager.findFragmentById(R.id.playerFragment)
                    as PlayerFragment

        playerFragment.updateSong(songs[currentIndex])
    }

    override fun onNextSong() {
        currentIndex++
        if (currentIndex >= songs.size)
            currentIndex = 0
        updatePlayer()
    }

    override fun onPreviousSong() {
        currentIndex--
        if (currentIndex < 0)
            currentIndex = songs.size - 1
        updatePlayer()
    }
}