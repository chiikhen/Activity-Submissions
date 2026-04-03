package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), SongNavigator {

    private var currentSongIndex = 0

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }
    }

    private fun getPlayerFragment(): PlayerFragment? {
        return supportFragmentManager.findFragmentById(R.id.playerFragmentContainer) as? PlayerFragment
    }

    override fun onSongSelected(position: Int) {
        currentSongIndex = position
        getPlayerFragment()?.loadSong(songs[currentSongIndex])
    }

    override fun onPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--
        } else {
            currentSongIndex = songs.size - 1
        }
        getPlayerFragment()?.loadSong(songs[currentSongIndex])
    }

    override fun onNextSong() {
        if (currentSongIndex < songs.size - 1) {
            currentSongIndex++
        } else {
            currentSongIndex = 0
        }
        getPlayerFragment()?.loadSong(songs[currentSongIndex])
    }
}
