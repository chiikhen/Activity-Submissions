package com.example.exercise_2_music_player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton

class SongAdapter(
    private val songs: List<Song>,
    private val onSongClick: (Song) -> Unit,
    private val onFavoriteClick: (Song) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = songs.size
    override fun getItem(position: Int): Any = songs[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_song, parent, false)

        val song = songs[position]
        view.findViewById<TextView>(R.id.songTitle).text = song.title
        view.findViewById<TextView>(R.id.songArtist).text = song.artist
        view.findViewById<TextView>(R.id.songDuration).text = song.duration

        val favoriteButton = view.findViewById<ImageButton>(R.id.favoriteButton)
        favoriteButton.setImageResource(
            if (song.isFavorite) R.drawable.ic_menu_favorite
            else R.drawable.ic_menu_favorite // Update this to an outline if available
        )

        favoriteButton.setOnClickListener {
            onFavoriteClick(song)
            notifyDataSetChanged()
        }

        view.setOnClickListener { onSongClick(song) }

        return view
    }
}