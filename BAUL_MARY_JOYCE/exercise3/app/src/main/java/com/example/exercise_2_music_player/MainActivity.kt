package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), MusicNavigationListener {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.listContainer, MusicListFragment())
                // Fixed to use the new class name
                .replace(R.id.playerContainer, ManageSong(), "PLAYER_TAG")
                .commit()
        }
    }

    override fun onMusicSelected(index: Int) {
        currentIndex = index
        updatePlayer()
    }

    override fun onNextPressed() {
        currentIndex = (currentIndex + 1) % songs.size
        updatePlayer()
    }

    override fun onPreviousPressed() {
        currentIndex = if (currentIndex > 0) currentIndex - 1 else songs.size - 1
        updatePlayer()
    }

    private fun updatePlayer() {
        val playerFragment = supportFragmentManager.findFragmentByTag("PLAYER_TAG") as? ManageSong

        val songData = songs[currentIndex]
        val parts = songData.split(" - ")
        val title = parts.getOrNull(0) ?: ""
        val url = parts.getOrNull(1) ?: ""

        playerFragment?.playMusic(title, url)
    }
}