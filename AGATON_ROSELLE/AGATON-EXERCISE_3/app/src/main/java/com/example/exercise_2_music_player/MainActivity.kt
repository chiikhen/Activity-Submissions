package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), MusicPlayerInterface {

    private lateinit var musicListFragment: MusicListFragment
    private lateinit var musicPlayerFragment: MusicPlayerFragment

    // List of songs (same as in MusicListFragment)
    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    private var currentSongPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        // Only create fragments if this is the first creation
        if (savedInstanceState == null) {
            setupFragments()
        } else {
            // Restore fragment references after configuration change
            musicListFragment = supportFragmentManager.findFragmentById(R.id.musicListFragmentContainer)
                    as MusicListFragment
            musicPlayerFragment = supportFragmentManager.findFragmentById(R.id.musicPlayerFragmentContainer)
                    as MusicPlayerFragment
        }
    }

    private fun setupFragments() {
        // Create fragment instances
        musicListFragment = MusicListFragment.newInstance()
        musicPlayerFragment = MusicPlayerFragment.newInstance()

        // Add fragments to their containers
        supportFragmentManager.beginTransaction()
            .replace(R.id.musicListFragmentContainer, musicListFragment)
            .replace(R.id.musicPlayerFragmentContainer, musicPlayerFragment)
            .commit()
    }

    /**
     * Called when a song is selected from the MusicListFragment
     */
    override fun onSongSelected(songUrl: String, position: Int) {
        currentSongPosition = position
        musicPlayerFragment.loadSong(songUrl, position)
    }

    /**
     * Called when the Next button is pressed in MusicPlayerFragment
     */
    override fun onNextSong() {
        // Move to next song, wrap around to first song if at the end
        currentSongPosition = (currentSongPosition + 1) % songs.size
        val nextSong = songs[currentSongPosition]
        musicPlayerFragment.loadSong(nextSong, currentSongPosition)
    }

    /**
     * Called when the Previous button is pressed in MusicPlayerFragment
     */
    override fun onPreviousSong() {
        // Move to previous song, wrap around to last song if at the beginning
        currentSongPosition = if (currentSongPosition - 1 < 0) {
            songs.size - 1
        } else {
            currentSongPosition - 1
        }
        val previousSong = songs[currentSongPosition]
        musicPlayerFragment.loadSong(previousSong, currentSongPosition)
    }
}