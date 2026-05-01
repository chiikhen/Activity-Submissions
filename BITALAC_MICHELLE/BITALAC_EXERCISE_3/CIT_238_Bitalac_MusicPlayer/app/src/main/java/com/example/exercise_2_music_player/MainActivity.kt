package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(),
    SongListFragment.OnSongSelectedListener {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
        "Song 4 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
        "Song 5 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3"
    )

    private var currentPosition = 0
    private lateinit var playerFragment: ManageSongFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listFragment = SongListFragment(songs)
        playerFragment = ManageSongFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.listContainer, listFragment)
            .replace(R.id.playerContainer, playerFragment)
            .commit()
    }

    // Interface callback when song selected
    override fun onSongSelected(position: Int) {
        currentPosition = position
        val url = songs[position].substringAfter(" - ")
        playerFragment.playSong(songs[position], url)
    }

    override fun onNextRequested() {
        currentPosition = (currentPosition + 1) % songs.size
        onSongSelected(currentPosition)
    }

    override fun onPreviousRequested() {
        currentPosition =
            if (currentPosition - 1 < 0) songs.size - 1
            else currentPosition - 1
        onSongSelected(currentPosition)
    }
}