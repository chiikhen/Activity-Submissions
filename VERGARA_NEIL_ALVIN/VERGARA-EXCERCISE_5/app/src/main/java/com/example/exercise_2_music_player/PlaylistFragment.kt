package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class PlaylistFragment : Fragment() {
    private lateinit var songsListView: ListView
    private lateinit var listener: MusicPlayerListener
    private var currentSongIndex = 0

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as MusicPlayerListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songsListView = view.findViewById(R.id.songsListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs.map { it.substringBefore(" - ") })
        songsListView.adapter = adapter

        songsListView.setOnItemClickListener { _, _, position, _ ->
            currentSongIndex = position
            val songData = songs[position]
            val title = songData.substringBefore(" - ")
            val url = songData.substringAfter(" - ")
            listener.onSongSelected(title, url)
        }
    }

    fun previousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--
        } else {
            currentSongIndex = songs.size - 1
        }
        playSongAtIndex(currentSongIndex)
    }

    fun nextSong() {
        if (currentSongIndex < songs.size - 1) {
            currentSongIndex++
        } else {
            currentSongIndex = 0
        }
        playSongAtIndex(currentSongIndex)
    }

    private fun playSongAtIndex(index: Int) {
        val songData = songs[index]
        val title = songData.substringBefore(" - ")
        val url = songData.substringAfter(" - ")
        listener.onSongSelected(title, url)
        songsListView.setSelection(index)
    }
}