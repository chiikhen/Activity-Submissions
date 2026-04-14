package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ArrayAdapter
import android.widget.ListView
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var songListView: ListView
    private lateinit var songs: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        songListView = findViewById(R.id.songListView)

        // Initialize songs array with sample data (format: "Song Name - URL")
        songs = arrayOf(
            "Song 1 - https://example.com/song1.mp3",
            "Song 2 - https://example.com/song2.mp3",
            "Song 3 - https://example.com/song3.mp3"
        )

        // Setup adapter for the ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, songs)
        songListView.adapter = adapter

        // Setup setOnItemClickListener for song selection
        songListView.setOnItemClickListener { parent, view, position, id ->
            val selectedSong = songs[position]
            val intent = Intent(this, ManageSong::class.java)
            intent.putExtra("EXTRA_SONG_DATA", selectedSong)
            startActivity(intent)
        }
    }
}