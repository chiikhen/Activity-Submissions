package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), MusicPlayerListener {

    private var currentPosition: Int = 0
    private lateinit var songs: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        songs = listOf(
            "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
        )
    }

    override fun onSongSelected(position: Int) {
        currentPosition = position
        playCurrentSong()
    }

    override fun onNextRequested() {
        if (currentPosition < songs.size - 1) {
            currentPosition++
            playCurrentSong()
        }
    }

    override fun onPreviousRequested() {
        if (currentPosition > 0) {
            currentPosition--
            playCurrentSong()
        }
    }

    private fun playCurrentSong() {
        val songData = songs[currentPosition]
        val parts = songData.split(" - ")
        if (parts.size == 2) {
            val title = parts[0]
            val url = parts[1]
            val playerFragment = supportFragmentManager.findFragmentById(R.id.player_container) as? MusicPlayerFragment
            playerFragment?.playSong(title, url)
        }
    }
}
