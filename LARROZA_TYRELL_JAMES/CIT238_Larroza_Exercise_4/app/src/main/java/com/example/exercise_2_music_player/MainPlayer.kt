package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class MainPlayer : Fragment(), SongListFragment.OnSongSelectedListener, PlayerFragment.PlayerControls {
    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )
    private var currentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_player, container, false)
    }

    private fun getPlayerFragment() = childFragmentManager.findFragmentById(R.id.playerFragment) as? PlayerFragment

    override fun onSongSelected(song: String) {
        currentIndex = songs.indexOf(song)
        getPlayerFragment()?.updateSong(songs[currentIndex])
    }

    override fun onNextSong() {
        currentIndex = (currentIndex + 1) % songs.size
        getPlayerFragment()?.updateSong(songs[currentIndex])
    }

    override fun onPreviousSong() {
        currentIndex = if (currentIndex - 1 < 0) songs.size - 1 else currentIndex - 1
        getPlayerFragment()?.updateSong(songs[currentIndex])
    }
}