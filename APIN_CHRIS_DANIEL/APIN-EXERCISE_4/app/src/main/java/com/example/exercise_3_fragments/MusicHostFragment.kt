package com.example.exercise_3_fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class MusicHostFragment : Fragment(R.layout.fragment_music_host),
    MusicListFragment.OnSongSelectedListener,
    MusicPlayerFragment.OnNavigationListener {

    private lateinit var listFragment: MusicListFragment
    private lateinit var playerFragment: MusicPlayerFragment
    private var currentIndex = 0

    private val songs: List<String>
        get() = listFragment.getSongs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            listFragment = MusicListFragment()
            playerFragment = MusicPlayerFragment()

            childFragmentManager.beginTransaction()
                .replace(R.id.musicListContainer, listFragment)
                .replace(R.id.musicPlayerContainer, playerFragment)
                .commit()
        } else {
            listFragment = childFragmentManager.findFragmentById(R.id.musicListContainer) as? MusicListFragment
                ?: MusicListFragment()
            playerFragment = childFragmentManager.findFragmentById(R.id.musicPlayerContainer) as? MusicPlayerFragment
                ?: MusicPlayerFragment()
        }
    }

    override fun onSongSelected(songData: String, position: Int) {
        currentIndex = position
        playerFragment.loadSong(songData)
    }

    override fun onPreviousSong() {
        if (songs.isNotEmpty()) {
            currentIndex = (currentIndex - 1 + songs.size) % songs.size
            playerFragment.loadSong(songs[currentIndex])
        }
    }

    override fun onNextSong() {
        if (songs.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % songs.size
            playerFragment.loadSong(songs[currentIndex])
        }
    }
}