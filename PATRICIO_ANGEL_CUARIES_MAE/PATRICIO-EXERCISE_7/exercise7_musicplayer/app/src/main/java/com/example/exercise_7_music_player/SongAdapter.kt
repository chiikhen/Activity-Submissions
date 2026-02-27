package com.example.exercise_7_music_player

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class SongAdapter(
    context: Context,
    private val songs: List<String>,
    private val rawIds: List<Int>
) : ArrayAdapter<String>(context, R.layout.item_song, songs) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_song, parent, false)

        val songTitle = view.findViewById<TextView>(R.id.songItemTitle)
        val btnFavorite = view.findViewById<ImageButton>(R.id.btnFavorite)

        songTitle.text = songs[position]

        (view as ViewGroup).descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS

        val isFav = FavoritesManager.isFavorite(songs[position])
        btnFavorite.setImageResource(R.drawable.ic_favorite)
        btnFavorite.setColorFilter(
            if (isFav)
                android.graphics.Color.parseColor("#6A4C9C")
            else
                android.graphics.Color.parseColor("#CCCCCC")
        )

        btnFavorite.setOnClickListener {
            if (FavoritesManager.isFavorite(songs[position])) {
                FavoritesManager.remove(songs[position], rawIds[position])
                btnFavorite.setColorFilter(android.graphics.Color.parseColor("#CCCCCC"))
                Toast.makeText(
                    context,
                    "${songs[position]} removed from Favorites",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                FavoritesManager.add(songs[position], rawIds[position])
                btnFavorite.setColorFilter(android.graphics.Color.parseColor("#6A4C9C"))
                Toast.makeText(
                    context,
                    "${songs[position]} added to Favorites!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return view
    }
}