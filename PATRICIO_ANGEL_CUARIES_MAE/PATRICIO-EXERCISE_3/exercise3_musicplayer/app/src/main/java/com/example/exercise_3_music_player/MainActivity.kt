package com.example.exercise_3_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), MusicplayerListener {

    private lateinit var songListFragment: SongListFragment
    private lateinit var manageSongFragment: ManageSong

    private var currentSongIndex: Int = 0

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
            songListFragment  = SongListFragment()
            manageSongFragment = ManageSong()

            supportFragmentManager.beginTransaction()
                .replace(R.id.songListContainer, songListFragment)
                .replace(R.id.musicPlayerContainer, manageSongFragment)
                .commit()
        } else {
            songListFragment   = supportFragmentManager.findFragmentById(R.id.songListContainer) as SongListFragment
            manageSongFragment = supportFragmentManager.findFragmentById(R.id.musicPlayerContainer) as ManageSong
        }
    }

    override fun onSongSelected(songTitle: String, songUrl: String, position: Int) {
        currentSongIndex = position
        manageSongFragment.loadSong(songTitle, songUrl)
    }

    override fun onPreviousSong() {
        val songs = songListFragment.songs
        if (songs.isEmpty()) return
        currentSongIndex = (currentSongIndex - 1 + songs.size) % songs.size
        loadSongAtIndex(currentSongIndex)
    }

    override fun onNextSong() {
        val songs = songListFragment.songs
        if (songs.isEmpty()) return
        currentSongIndex = (currentSongIndex + 1) % songs.size
        loadSongAtIndex(currentSongIndex)
    }

    private fun loadSongAtIndex(index: Int) {
        val songs = songListFragment.songs
        val selectedSong = songs[index]
        val title = selectedSong.substringBefore(" - ")
        val url   = selectedSong.substringAfter(" - ")
        manageSongFragment.loadSong(title, url)
    }
}