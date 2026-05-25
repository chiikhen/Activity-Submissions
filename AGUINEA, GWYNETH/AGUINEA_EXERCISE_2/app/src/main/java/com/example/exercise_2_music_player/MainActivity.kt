package com.example.exercise_2_music_player

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var songsListView: ListView
    // Updated song list with Spotify tracks (formatted for your current ManageSong logic)
    private val songs = listOf(
        "Blinding Lights - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Stay - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Heat Waves - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "Starboy - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        // Using custom layout R.layout.list_item_song to support white text on dark background
        val adapter = ArrayAdapter(this, R.layout.list_item_song, songs)
        songsListView = findViewById(R.id.songsListView)
        songsListView.adapter = adapter

        // Click listener to open ManageSong
        songsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedSong = songs[position]
            val intent = Intent(this, ManageSong::class.java).apply {
                putExtra("EXTRA_SONG_DATA", selectedSong)
            }
            startActivity(intent)
        }
    }
}
