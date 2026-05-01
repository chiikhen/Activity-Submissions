package com.example.exercise_2_music_player

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class SongAdapter(
    private val context: Context,
    private val songs: List<Song>
) : BaseAdapter() {

    // Tracks which position is currently playing (-1 = none)
    var currentPlayingPosition: Int = -1
        set(value) {
            field = value
            notifyDataSetChanged() // Refresh list to update highlight
        }

    override fun getCount(): Int = songs.size

    override fun getItem(position: Int): Any = songs[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.song_item, parent, false)

        val song = songs[position]
        val isPlaying = position == currentPlayingPosition

        val songItemRoot    = view.findViewById<LinearLayout>(R.id.songItemRoot)
        val albumArtImageView = view.findViewById<ImageView>(R.id.albumArtImageView)
        val songNameText    = view.findViewById<TextView>(R.id.songNameText)
        val songArtistText  = view.findViewById<TextView>(R.id.songArtistText)
        val playingBadge    = view.findViewById<TextView>(R.id.playingBadge)
        val equalizerIcon   = view.findViewById<ImageView>(R.id.equalizerIcon)
        val playIcon        = view.findViewById<ImageView>(R.id.playIcon)

        // Set content
        albumArtImageView.setImageResource(song.albumArtResId)
        songNameText.text = song.name
        songArtistText.text = song.artist

        if (isPlaying) {
            // Highlight: purple tinted background, purple song name, show equalizer
            songItemRoot.setBackgroundResource(R.drawable.bg_song_item_playing)
            songNameText.setTextColor(context.getColor(R.color.purple_light))
            playingBadge.visibility = View.VISIBLE
            equalizerIcon.visibility = View.VISIBLE
            playIcon.visibility = View.GONE
        } else {
            // Normal state
            songItemRoot.setBackgroundResource(R.drawable.bg_song_item_ripple)
            songNameText.setTextColor(context.getColor(R.color.text_primary))
            playingBadge.visibility = View.GONE
            equalizerIcon.visibility = View.GONE
            playIcon.visibility = View.VISIBLE
        }

        return view
    }
}
