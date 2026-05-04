package com.example.tabares_exercise_2

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class MyMusicFragment : Fragment(R.layout.fragment_my_music) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (childFragmentManager.findFragmentById(R.id.songListContainer) == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.songListContainer, SongListFragment())
                .commitNow()
        }
    }
}
