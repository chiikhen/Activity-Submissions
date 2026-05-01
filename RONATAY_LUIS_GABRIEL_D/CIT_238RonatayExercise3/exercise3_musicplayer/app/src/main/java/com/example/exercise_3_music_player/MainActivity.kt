package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), MusicListFragment.OnMusicInteractionListener {

    private val musicList = listOf(
        Music("Song 1", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
        Music("Song 2", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"),
        Music("Song 3", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3")
    )

    private var currentIndex = -1
    private var musicListFragment: MusicListFragment? = null
    private var musicPlayerFragment: MusicPlayerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt("currentIndex", -1)
        }

        if (savedInstanceState == null) {
            musicListFragment = MusicListFragment().also {
                it.setMusicList(musicList)
            }

            musicPlayerFragment = MusicPlayerFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.listFragmentContainer, musicListFragment!!)
                .replace(R.id.playerFragmentContainer, musicPlayerFragment!!)
                .commit()
        } else {
            musicListFragment = supportFragmentManager
                .findFragmentById(R.id.listFragmentContainer) as? MusicListFragment
            musicPlayerFragment = supportFragmentManager
                .findFragmentById(R.id.playerFragmentContainer) as? MusicPlayerFragment

            musicListFragment?.setMusicList(musicList)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIndex", currentIndex)
    }

    override fun onMusicSelected(music: Music, position: Int) {
        currentIndex = position
        musicPlayerFragment?.playMusic(music)
    }

    override fun onNextRequested() {
        if (musicList.isEmpty()) return
        currentIndex = (currentIndex + 1) % musicList.size
        musicPlayerFragment?.playMusic(musicList[currentIndex])
    }

    override fun onPreviousRequested() {
        if (musicList.isEmpty()) return
        currentIndex = if (currentIndex - 1 < 0) musicList.size - 1 else currentIndex - 1
        musicPlayerFragment?.playMusic(musicList[currentIndex])
    }
}
