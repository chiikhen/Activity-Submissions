package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class ListFragment : Fragment() {
    private var listener: MusicPlayerInterface? = null
    private lateinit var songsListView: ListView
    private lateinit var songAdapter: SongAdapter
    private var songs: List<Song> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MusicPlayerInterface) {
            listener = context
        } else {
            throw RuntimeException("$context must implement MusicPlayerInterface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songsListView = view.findViewById(R.id.songsListView)

        // Get songs from MainActivity
        songs = (activity as? MainActivity)?.getSongs() ?: emptyList()

        songAdapter = SongAdapter(requireContext(), songs)
        songsListView.adapter = songAdapter

        songsListView.setOnItemClickListener { _, _, position, _ ->
            listener?.onSongSelected(position)
        }
    }

    /** Called by MainActivity to update which song is highlighted as playing */
    fun setPlayingPosition(position: Int) {
        if (::songAdapter.isInitialized) {
            songAdapter.currentPlayingPosition = position
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
