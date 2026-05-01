package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class SongListFragment : Fragment() {

    interface Callback {
        fun onSongSelected(index: Int)
    }

    private var callback: Callback? = null
    private lateinit var songsListView: ListView
    private var songs: List<String> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? Callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songs = arguments?.getStringArrayList(ARG_SONGS) ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs)
        songsListView = view.findViewById(R.id.songsListView)
        songsListView.adapter = adapter

        songsListView.setOnItemClickListener { _, _, position, _ ->
            callback?.onSongSelected(position)
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    companion object {
        private const val ARG_SONGS = "ARG_SONGS"

        fun newInstance(songs: ArrayList<String>): SongListFragment {
            val fragment = SongListFragment()
            fragment.arguments = Bundle().apply {
                putStringArrayList(ARG_SONGS, songs)
            }
            return fragment
        }
    }
}

