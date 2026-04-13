package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class FavoritesFragment : Fragment() {

    // Favorite songs list — curated personal favorites
    private val favorites = listOf(
        "The Nights - https://dn710000.ca.archive.org/0/items/01-avicii-the-nights-audio/01%20-%20Avicii%20-%20The%20Nights%20%28Audio%29.mp3",
        "Clarity - https://dn720306.ca.archive.org/0/items/clarity-by-zedd-ft.-foxes-lyrics-official-2026614/Clarity-By-Zedd-ft.-Foxes-Lyrics-Official_2026614.mp3"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val favoritesListView = view.findViewById<ListView>(R.id.favoritesListView)
        val adapter = FavoriteSongAdapter(requireContext(), favorites)
        favoritesListView.adapter = adapter

        favoritesListView.setOnItemClickListener { _, _, position, _ ->
            val bundle = bundleOf("songData" to favorites[position])
            findNavController().navigate(R.id.nav_now_playing, bundle)
        }
    }

    private class FavoriteSongAdapter(context: android.content.Context, private val songs: List<String>) :
        ArrayAdapter<String>(context, R.layout.song_list_item, songs) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.song_list_item, parent, false)

            val songData = songs[position]
            val songName = songData.substringBefore(" - ")

            view.findViewById<TextView>(android.R.id.text1).text = songName
            view.findViewById<TextView>(android.R.id.text2).text = "❤ Favorite"

            return view
        }
    }
}
