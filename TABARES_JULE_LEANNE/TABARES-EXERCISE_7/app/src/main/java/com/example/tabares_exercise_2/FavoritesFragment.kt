package com.example.tabares_exercise_2

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var listener: OnSongInteractionListener
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var adapter: BaseAdapter
    private var favorites: List<Song> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSongInteractionListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.favoritesListView)
        emptyView = view.findViewById(R.id.emptyFavoritesText)

        adapter = object : BaseAdapter() {
            override fun getCount(): Int = favorites.size
            override fun getItem(position: Int): Any = favorites[position]
            override fun getItemId(position: Int): Long = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                val row = convertView ?: layoutInflater.inflate(R.layout.favorite_item, parent, false)
                val song = favorites[position]

                row.findViewById<ImageView>(R.id.favoriteAlbumArt).setImageResource(song.imageRes)
                row.findViewById<TextView>(R.id.favoriteTitle).text = song.name
                row.findViewById<TextView>(R.id.favoriteArtist).text = song.artist

                return row
            }
        }

        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val song = favorites[position]
            val songIndex = SongListFragment.SONGS.indexOf(song).takeIf { it >= 0 } ?: 0
            listener.onSongSelected(song, songIndex)
        }

        refreshFavorites()
    }

    override fun onResume() {
        super.onResume()
        refreshFavorites()
    }

    private fun refreshFavorites() {
        favorites = listener.getFavoriteSongs()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }

        if (::emptyView.isInitialized) {
            emptyView.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
