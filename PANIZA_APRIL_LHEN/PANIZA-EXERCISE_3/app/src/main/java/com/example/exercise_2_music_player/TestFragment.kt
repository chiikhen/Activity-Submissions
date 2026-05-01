// Test file to verify compilation
package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

class TestFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Test if R.layout works
        val layoutId = R.layout.fragment_music_player
        val layoutId2 = R.layout.fragment_song_list
    }
}
