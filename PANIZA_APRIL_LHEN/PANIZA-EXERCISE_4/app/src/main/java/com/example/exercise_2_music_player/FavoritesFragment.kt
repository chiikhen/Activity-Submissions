package com.example.exercise_2_music_player

import android.content.res.ColorStateList
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.Locale

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var favoritesListView: ListView
    private var musicPlayerInterface: MusicPlayerInterface? = null
    private var favoriteSongs: List<String> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicPlayerInterface) {
            musicPlayerInterface = context
        } else {
            throw RuntimeException("$context must implement MusicPlayerInterface")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesListView = view.findViewById(R.id.favoritesListView)
        refreshFavorites()

        favoritesListView.setOnItemClickListener { _, _, position, _ ->
            val songIndex =
                musicPlayerInterface?.getSongList()?.indexOf(favoriteSongs[position]) ?: -1
            if (songIndex >= 0) {
                musicPlayerInterface?.onSongSelected(songIndex)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshFavorites()
    }

    private fun refreshFavorites() {
        favoriteSongs = musicPlayerInterface?.getFavoriteSongs().orEmpty()
        favoritesListView.adapter = FavoriteSongsAdapter(favoriteSongs)

        view?.findViewById<TextView>(R.id.favoriteCountBadge)?.text =
            String.format(Locale.getDefault(), getString(R.string.song_count), favoriteSongs.size)
    }

    inner class FavoriteSongsAdapter(private val songList: List<String>) : BaseAdapter() {

        override fun getCount(): Int = songList.size

        override fun getItem(position: Int): Any = songList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView = convertView ?: LayoutInflater.from(requireContext())
                .inflate(R.layout.item_song, parent, false)

            val songData = songList[position]
            val songTitle = songData.substringBefore("|")
            val artistName = songData.substringAfter("|").substringBefore(" - ")
            val mainSongIndex = musicPlayerInterface?.getSongList()?.indexOf(songData) ?: -1
            val actionIcon = rowView.findViewById<ImageView>(R.id.playIcon)

            rowView.findViewById<TextView>(R.id.songTitle).text = songTitle
            rowView.findViewById<TextView>(R.id.songArtist).text = artistName
            actionIcon.setImageResource(R.drawable.ic_favorite)
            actionIcon.contentDescription = getString(R.string.remove_from_favorites)
            actionIcon.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.accent)
            )
            actionIcon.setOnClickListener {
                if (mainSongIndex >= 0) {
                    musicPlayerInterface?.toggleFavoriteSong(mainSongIndex)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.song_removed_from_favorites, songTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    refreshFavorites()
                }
            }

            return rowView
        }
    }

    override fun onDetach() {
        super.onDetach()
        musicPlayerInterface = null
    }
}
