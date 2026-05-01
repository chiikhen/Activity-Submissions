package com.example.exercise_2_music_player

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.content.Intent
import android.net.Uri

class MainActivity : AppCompatActivity(), SelectedSongListener {

    private var currentIndex = 0

    // Your original song data
    private val songs = listOf(
        "Clarity" to "https://drive.google.com/uc?export=download&id=14j6dm05QQ91zp5vyClDgBbCmnOOezaTX",
        "Konsensya" to "https://drive.google.com/uc?export=download&id=1rQLvFQM-9J274cR0n5nsRsd89vpT5-ev",
        "Aura" to "https://drive.google.com/uc?export=download&id=1HkI9vgKPnMXussNjyKnO1NdcJNojlhlz"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Make sure activity_main.xml has FrameLayouts with these IDs:
        // @+id/list_container and @+id/player_container
    }

    override fun onSongSelected(index: Int) {
        currentIndex = index
        updatePlayerFragment()
    }

    override fun onNextSong() {
        // Logic to get the next song (loops back to start if at the end)
        currentIndex = (currentIndex + 1) % songs.size
        updatePlayerFragment()
    }

    override fun onPreviousSong() {
        // Logic to get the previous song (loops to end if at the start)
        currentIndex = if (currentIndex > 0) currentIndex - 1 else songs.size - 1
        updatePlayerFragment()
    }

    // Inside MainActivity.kt
    private fun updatePlayerFragment() {
        val playerFrag = supportFragmentManager.findFragmentById(R.id.player_container) as? ManageSong

        // In your new code, 'songs' is a list of Pairs (title to url)
        // songs[currentIndex] returns one Pair
        val (songTitle, songUrl) = songs[currentIndex]

        // Now just pass them directly!
        playerFrag?.loadNewSong(songTitle, songUrl)
    }




}