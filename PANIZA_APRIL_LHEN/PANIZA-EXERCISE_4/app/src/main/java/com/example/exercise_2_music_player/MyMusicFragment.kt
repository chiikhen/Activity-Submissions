package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class MyMusicFragment : Fragment(R.layout.fragment_music_list_host) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (childFragmentManager.findFragmentById(R.id.songListHostContainer) == null) {
            childFragmentManager.beginTransaction()
                .replace(
                    R.id.songListHostContainer,
                    SongListFragment.newInstance(enableFavoriteSelection = true)
                )
                .commit()
        }
    }
}
