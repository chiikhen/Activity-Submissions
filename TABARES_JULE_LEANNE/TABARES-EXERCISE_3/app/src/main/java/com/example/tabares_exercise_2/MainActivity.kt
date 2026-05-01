package com.example.tabares_exercise_2

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), OnSongInteractionListener {

    private lateinit var songListFragment: SongListFragment
    private lateinit var songPlayerFragment: SongPlayerFragment
    private var currentSongIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        getSharedPreferences("MusicPlayerPrefs", Context.MODE_PRIVATE).edit().clear().apply()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            songListFragment = SongListFragment()
            songPlayerFragment = SongPlayerFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.songListContainer, songListFragment)
                .replace(R.id.songPlayerContainer, songPlayerFragment)
                .commit()
        } else {
            songListFragment = supportFragmentManager
                .findFragmentById(R.id.songListContainer) as SongListFragment
            songPlayerFragment = supportFragmentManager
                .findFragmentById(R.id.songPlayerContainer) as SongPlayerFragment
        }
    }

    override fun onSongSelected(song: Song, position: Int) {
        currentSongIndex = position
        songPlayerFragment.loadSong(song)
    }

    override fun onNextSong() {
        val list = songListFragment.songList
        if (list.isEmpty()) return
        currentSongIndex = (currentSongIndex + 1) % list.size
        songPlayerFragment.loadSong(list[currentSongIndex])
    }

    override fun onPreviousSong() {
        val list = songListFragment.songList
        if (list.isEmpty()) return
        currentSongIndex = if (currentSongIndex <= 0) list.size - 1 else currentSongIndex - 1
        songPlayerFragment.loadSong(list[currentSongIndex])
    }
}
