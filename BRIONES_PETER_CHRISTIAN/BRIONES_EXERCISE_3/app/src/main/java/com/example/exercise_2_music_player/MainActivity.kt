package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), MusicPlayerInterface {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )
    private var currentIndex = -1

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

    override fun onSongSelected(songData: String) {
        currentIndex = songs.indexOf(songData)
        updatePlayer(songData)
    }

    override fun onNextRequested() {
        if (songs.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % songs.size
            updatePlayer(songs[currentIndex])
        }
    }

    override fun onPreviousRequested() {
        if (songs.isNotEmpty()) {
            currentIndex = if (currentIndex <= 0) songs.size - 1 else currentIndex - 1
            updatePlayer(songs[currentIndex])
        }
    }

    private fun updatePlayer(songData: String) {
        val playerFragment = supportFragmentManager.findFragmentById(R.id.player_container) as? MusicPlayerFragment
        playerFragment?.playSong(songData)
    }
}
