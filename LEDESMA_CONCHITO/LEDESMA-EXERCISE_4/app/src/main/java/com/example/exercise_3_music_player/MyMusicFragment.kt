package com.example.exercise_3_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class MyMusicFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val songsListView = view.findViewById<ListView>(R.id.myMusicListView)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            MusicRepository.songs.map { it.title }
        )
        songsListView.adapter = adapter

        songsListView.setOnItemClickListener { _, _, position, _ ->
            MusicRepository.selectSong(position)
            findNavController().navigate(R.id.nav_now_playing)
        }
    }
}