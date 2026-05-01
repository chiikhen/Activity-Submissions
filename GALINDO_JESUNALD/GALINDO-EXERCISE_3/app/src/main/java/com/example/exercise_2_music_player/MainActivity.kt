package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), SongControlListener {

    val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    private lateinit var playerFragment: PlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        // Initialize fragments if this is the first creation
        if (savedInstanceState == null) {
            val listFragment = MusicListFragment()
            playerFragment = PlayerFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.listContainer, listFragment)
                .replace(R.id.playerContainer, playerFragment)
                .commit()
        } else {
            // Retrieve the existing fragment if recreating (e.g., screen rotation)
            playerFragment = supportFragmentManager.findFragmentById(R.id.playerContainer) as PlayerFragment
        }
    }

    // Interface Implementations
    //for PR
    override fun onSongSelected(songData: String, position: Int) {
        playerFragment.loadSong(songData, position)
    }

    override fun onNextSong(currentPosition: Int) {
        val nextPosition = if (currentPosition < songs.size - 1) currentPosition + 1 else 0
        playerFragment.loadSong(songs[nextPosition], nextPosition)
    }

    override fun onPreviousSong(currentPosition: Int) {
        val prevPosition = if (currentPosition > 0) currentPosition - 1 else songs.size - 1
        playerFragment.loadSong(songs[prevPosition], prevPosition)
    }
}