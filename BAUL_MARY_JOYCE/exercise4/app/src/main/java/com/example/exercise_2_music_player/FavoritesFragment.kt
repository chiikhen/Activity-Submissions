package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment() {

    companion object {
        val favoriteSongs = mutableListOf<String>()
    }

    private lateinit var favListTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        favListTextView = view.findViewById(R.id.favListTextView)

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        favListTextView.text = if (favoriteSongs.isEmpty()) {
            "No favorites yet!"
        } else {
            favoriteSongs.joinToString("\n")
        }
    }
}