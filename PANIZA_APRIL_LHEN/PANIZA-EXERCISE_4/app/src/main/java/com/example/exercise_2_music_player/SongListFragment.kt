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

class SongListFragment : Fragment() {

    private lateinit var songsListView: ListView
    private var musicPlayerInterface: MusicPlayerInterface? = null
    private val enableFavoriteSelection: Boolean
        get() = arguments?.getBoolean(ARG_ENABLE_FAVORITES, false) ?: false

    companion object {
        private const val ARG_ENABLE_FAVORITES = "arg_enable_favorites"

        fun newInstance(enableFavoriteSelection: Boolean = false): SongListFragment {
            return SongListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_ENABLE_FAVORITES, enableFavoriteSelection)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicPlayerInterface) {
            musicPlayerInterface = context
        } else {
            throw RuntimeException("$context must implement MusicPlayerInterface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songsListView = view.findViewById(R.id.songsListView)
        setupSongList()
    }

    private fun setupSongList() {
        musicPlayerInterface?.let { musicInterface ->
            val songs = musicInterface.getSongList()
            val adapter = SongAdapter(songs)
            songsListView.adapter = adapter

            // Update the song count badge
            view?.findViewById<TextView>(R.id.songCountBadge)?.text =
                String.format(Locale.getDefault(), getString(R.string.song_count), songs.size)

            songsListView.setOnItemClickListener { _, _, position, _ ->
                musicInterface.onSongSelected(position)
            }
        }
    }

    @Suppress("unused")
    fun updateSongList() {
        setupSongList()
    }

    // Custom adapter for song items
    inner class SongAdapter(private val songList: List<String>) : BaseAdapter() {

        override fun getCount(): Int = songList.size

        override fun getItem(position: Int): Any = songList[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(requireContext())
                .inflate(R.layout.item_song, parent, false)

            val songData = songList[position]
            val songTitle = songData.substringBefore("|")
            val artistName = songData.substringAfter("|").substringBefore(" - ")
            val actionIcon = view.findViewById<ImageView>(R.id.playIcon)

            view.findViewById<TextView>(R.id.songTitle).text = songTitle
            view.findViewById<TextView>(R.id.songArtist).text = artistName

            if (enableFavoriteSelection) {
                actionIcon.setImageResource(R.drawable.ic_favorite)
                actionIcon.contentDescription = getString(R.string.add_to_favorites)
                val isFavorite = musicPlayerInterface?.isSongFavorite(position) == true
                actionIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        if (isFavorite) R.color.accent else R.color.text_secondary
                    )
                )
                actionIcon.setOnClickListener {
                    val isFavoriteNow = musicPlayerInterface?.toggleFavoriteSong(position) == true
                    actionIcon.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            if (isFavoriteNow) R.color.accent else R.color.text_secondary
                        )
                    )
                    val message = if (isFavoriteNow) {
                        getString(R.string.song_added_to_favorites, songTitle)
                    } else {
                        getString(R.string.song_removed_from_favorites, songTitle)
                    }
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            } else {
                actionIcon.setImageResource(R.drawable.ic_play_arrow)
                actionIcon.contentDescription = getString(R.string.play_icon_description)
                actionIcon.setOnClickListener {
                    musicPlayerInterface?.onSongSelected(position)
                }
            }

            return view
        }
    }

    override fun onDetach() {
        super.onDetach()
        musicPlayerInterface = null
    }
}
