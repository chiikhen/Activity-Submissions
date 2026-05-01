package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

/**
 * Fragment that displays the list of available songs
 */
class MusicListFragment : Fragment() {

    private lateinit var songsListView: ListView
    private var musicPlayerInterface: MusicPlayerInterface? = null

    // List of songs
    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Make sure the activity implements the interface
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_music_list, container, false)

        // Setup the ListView
        songsListView = view.findViewById(R.id.songsListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs)
        songsListView.adapter = adapter

        // Set item click listener
        songsListView.setOnItemClickListener { parent, view, position, id ->
            val selectedSong = songs[position]
            // Notify the activity through the interface
            musicPlayerInterface?.onSongSelected(selectedSong, position)
        }

        return view
    }

    override fun onDetach() {
        super.onDetach()
        musicPlayerInterface = null
    }

    companion object {
        /**
         * Factory method to create a new instance of this fragment
         */
        fun newInstance(): MusicListFragment {
            return MusicListFragment()
        }
    }
}