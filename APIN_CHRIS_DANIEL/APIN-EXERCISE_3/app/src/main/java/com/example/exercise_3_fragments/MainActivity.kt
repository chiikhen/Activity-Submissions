package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(),
    MusicListFragment.OnSongSelectedListener,
    MusicPlayerFragment.OnNavigationListener {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    // Track the current song index for prev/next
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Add fragments only on first creation (avoid duplicates on rotation)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.musicListContainer, MusicListFragment())
                .replace(R.id.musicPlayerContainer, MusicPlayerFragment())
                .commit()
        }
    }

    // Called when user taps a song in MusicListFragment
    override fun onSongSelected(songData: String, position: Int) {
        currentIndex = position
        getPlayerFragment()?.loadSong(songData)
    }

    // Called when user taps "Previous" in MusicPlayerFragment
    override fun onPreviousSong() {
        currentIndex = (currentIndex - 1 + songs.size) % songs.size
        getPlayerFragment()?.loadSong(songs[currentIndex])
    }

    // Called when user taps "Next" in MusicPlayerFragment
    override fun onNextSong() {
        currentIndex = (currentIndex + 1) % songs.size
        getPlayerFragment()?.loadSong(songs[currentIndex])
    }

    private fun getPlayerFragment(): MusicPlayerFragment? {
        return supportFragmentManager.findFragmentById(R.id.musicPlayerContainer) as? MusicPlayerFragment
    }
}