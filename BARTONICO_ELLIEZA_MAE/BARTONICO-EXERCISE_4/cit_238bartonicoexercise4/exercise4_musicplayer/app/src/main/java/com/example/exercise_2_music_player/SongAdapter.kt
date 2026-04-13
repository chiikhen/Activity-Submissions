package com.example.exercise_2_music_player

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView

class SongAdapter(
    context: Context,
    private val songs: List<String>,
    private val onSongClick: (Int) -> Unit
) : ArrayAdapter<String>(context, R.layout.item_song, songs) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_song, parent, false)

        val song = songs[position]
        val songName = view.findViewById<TextView>(R.id.songName)
        val btnFavorite = view.findViewById<ImageButton>(R.id.btnFavorite)

        songName.text = song

        // Clicking the song name/row triggers play
        songName.setOnClickListener {
            onSongClick(position)
        }
        view.setOnClickListener {
            onSongClick(position)
        }

        // Set icon based on current favorite state
        updateStarIcon(btnFavorite, FavoritesManager.favorites.contains(song))

        btnFavorite.setOnClickListener {
            if (FavoritesManager.favorites.contains(song)) {
                FavoritesManager.favorites.remove(song)
            } else {
                FavoritesManager.favorites.add(song)
            }
            updateStarIcon(btnFavorite, FavoritesManager.favorites.contains(song))
        }

        return view
    }

    private fun updateStarIcon(btn: ImageButton, isFavorited: Boolean) {
        if (isFavorited) {
            btn.setImageResource(R.drawable.ic_favorite)
        } else {
            btn.setImageResource(R.drawable.ic_favorite_border)
        }
    }
}