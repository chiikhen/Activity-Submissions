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

    private var listener: OnSongSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnSongSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_song_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = requireActivity() as MainActivity
        val songNames = activity.songs.map { it.first }

        val listView = view.findViewById<ListView>(R.id.songsListView)
        listView.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songNames)

        listView.setOnItemClickListener { _, _, position, _ ->
            listener?.onSongSelected(position)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}