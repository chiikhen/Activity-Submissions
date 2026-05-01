package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), OnSongSelectedListener {

    private val songs = listOf(
        "The Nights - https://dn710000.ca.archive.org/0/items/01-avicii-the-nights-audio/01%20-%20Avicii%20-%20The%20Nights%20%28Audio%29.mp3",
        "Clarity - https://dn720306.ca.archive.org/0/items/clarity-by-zedd-ft.-foxes-lyrics-official-2026614/Clarity-By-Zedd-ft.-Foxes-Lyrics-Official_2026614.mp3",
        "Don't you worry child - https://dn721601.ca.archive.org/0/items/swedish-house-mafia-ft.-john-martin-dont-you-worry-child-official-video/Swedish%20House%20Mafia%20ft.%20John%20Martin%20-%20Don%27t%20You%20Worry%20Child%20%28Official%20Video%29.mp3"
    )

    private var currentSongIndex = 0

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

    private fun getPlayerFragment(): MusicPlayerFragment? {
        return supportFragmentManager.findFragmentById(R.id.playerFragmentContainer) as? MusicPlayerFragment
    }

    override fun onSongSelected(songData: String) {
        currentSongIndex = songs.indexOf(songData)
        getPlayerFragment()?.loadSong(songData)
    }

    override fun onPreviousSong() {
        if (songs.isEmpty()) return
        currentSongIndex = if (currentSongIndex - 1 < 0) songs.size - 1 else currentSongIndex - 1
        getPlayerFragment()?.loadSong(songs[currentSongIndex])
    }

    override fun onNextSong() {
        if (songs.isEmpty()) return
        currentSongIndex = (currentSongIndex + 1) % songs.size
        getPlayerFragment()?.loadSong(songs[currentSongIndex])
    }
}
