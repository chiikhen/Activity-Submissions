package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge

class MainActivity : AppCompatActivity(), OnSongSelectedListener {

    val songs = listOf(
        "Song 1" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3" to "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentSongList, SongListFragment())
                .replace(R.id.fragmentPlayer, PlayerFragment())
                .commit()
        }
    }

    override fun onSongSelected(index: Int) {
        currentIndex = index
        val (title, url) = songs[index]
        getPlayerFragment()?.loadSong(title, url)
    }

    override fun onPreviousSong() {
        currentIndex = (currentIndex - 1 + songs.size) % songs.size
        val (title, url) = songs[currentIndex]
        getPlayerFragment()?.loadSong(title, url)
    }

    override fun onNextSong() {
        currentIndex = (currentIndex + 1) % songs.size
        val (title, url) = songs[currentIndex]
        getPlayerFragment()?.loadSong(title, url)
    }

    private fun getPlayerFragment(): PlayerFragment? {
        return supportFragmentManager.findFragmentById(R.id.fragmentPlayer) as? PlayerFragment
    }
}