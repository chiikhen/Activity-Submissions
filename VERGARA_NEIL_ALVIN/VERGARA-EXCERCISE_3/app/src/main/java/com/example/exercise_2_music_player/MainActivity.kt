package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), MusicPlayerListener {

    private var playerFragment: PlayerFragment? = null
    private var playlistFragment: PlaylistFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        // Create fragments if they don't exist
        if (savedInstanceState == null) {
            playlistFragment = PlaylistFragment()
            playerFragment = PlayerFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.playlistContainer, playlistFragment!!)
                .replace(R.id.playerContainer, playerFragment!!)
                .commit()
        } else {
            playlistFragment = supportFragmentManager.findFragmentById(R.id.playlistContainer) as? PlaylistFragment
            playerFragment = supportFragmentManager.findFragmentById(R.id.playerContainer) as? PlayerFragment
        }
    }

    override fun onSongSelected(songTitle: String, songUrl: String) {
        playerFragment?.setSong(songTitle, songUrl)
    }

    override fun onPreviousSong() {
        playlistFragment?.previousSong()
    }

    override fun onNextSong() {
        playlistFragment?.nextSong()
    }
}