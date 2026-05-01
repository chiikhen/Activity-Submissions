package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), MusicPlayerInterface {

    private val songs = listOf(
        "Ambient Dreams|Relaxing Music - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Electric Vibes|Electronic Mix - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Jazz Cafe|Smooth Jazz - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "Summer Breeze|Chill Beats - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        "Night Drive|Lo-Fi Hip Hop - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
        "Ocean Waves|Nature Sounds - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3"
    )

    private var currentSongIndex: Int = 0
    private lateinit var songListFragment: SongListFragment
    private lateinit var musicPlayerFragment: MusicPlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        setupFragments()
    }

    private fun setupFragments() {
        songListFragment = SongListFragment()
        musicPlayerFragment = MusicPlayerFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.songListContainer, songListFragment)
            .replace(R.id.musicPlayerContainer, musicPlayerFragment)
            .commit()
    }

    // Implementation of MusicPlayerInterface
    override fun onSongSelected(songIndex: Int) {
        if (songIndex >= 0 && songIndex < songs.size) {
            currentSongIndex = songIndex
            musicPlayerFragment.loadAndPlaySong(songIndex)
        }
    }

    override fun onPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--
            musicPlayerFragment.loadAndPlaySong(currentSongIndex)
        }
    }

    override fun onNextSong() {
        if (currentSongIndex < songs.size - 1) {
            currentSongIndex++
            musicPlayerFragment.loadAndPlaySong(currentSongIndex)
        }
    }

    override fun getSongList(): List<String> {
        return songs
    }

    override fun getCurrentSongIndex(): Int {
        return currentSongIndex
    }
}
