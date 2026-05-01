package com.example.exercise_2_music_player

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit


class MainActivity : AppCompatActivity(), SongListFragment.Callback, SongPlayerFragment.Callback {

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
        "Song 4 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
        "Song 5 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3"
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(
                    R.id.songListContainer,
                    SongListFragment.newInstance(ArrayList(songs)),
                    TAG_SONG_LIST
                )
                add(
                    R.id.playerContainer,
                    SongPlayerFragment.newInstance(songs[currentIndex]),
                    TAG_SONG_PLAYER
                )
            }
            supportFragmentManager.executePendingTransactions()
        }

        getPlayerFragment()?.setSong(songs[currentIndex], false)
    }

    override fun onSongSelected(index: Int) {
        currentIndex = index
        getPlayerFragment()?.setSong(songs[currentIndex], true)
    }

    override fun onPreviousRequested() {
        if (songs.isEmpty()) return
        currentIndex = if (currentIndex == 0) songs.lastIndex else currentIndex - 1
        getPlayerFragment()?.setSong(songs[currentIndex], true)
    }

    override fun onNextRequested() {
        if (songs.isEmpty()) return
        currentIndex = if (currentIndex == songs.lastIndex) 0 else currentIndex + 1
        getPlayerFragment()?.setSong(songs[currentIndex], true)
    }

    private fun getPlayerFragment(): SongPlayerFragment? {
        return supportFragmentManager.findFragmentByTag(TAG_SONG_PLAYER) as? SongPlayerFragment
    }

    companion object {
        private const val TAG_SONG_LIST = "TAG_SONG_LIST"
        private const val TAG_SONG_PLAYER = "TAG_SONG_PLAYER"
    }
}