package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.Locale

class SongListFragment : Fragment() {

    private lateinit var songsListView: ListView
    private var musicPlayerInterface: MusicPlayerInterface? = null

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

            view.findViewById<TextView>(R.id.songTitle).text = songTitle
            view.findViewById<TextView>(R.id.songArtist).text = artistName

            return view
        }
    }

    override fun onDetach() {
        super.onDetach()
        musicPlayerInterface = null
    }
}
