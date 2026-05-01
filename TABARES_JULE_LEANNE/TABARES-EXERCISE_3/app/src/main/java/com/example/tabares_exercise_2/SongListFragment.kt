package com.example.tabares_exercise_2

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class SongListFragment : Fragment() {

    private lateinit var listener: OnSongInteractionListener

    val songList = listOf(
        Song("Love Me Not", "Ravyn Lenae", "https://uneven-apricot-mwxdnaxvop.edgeone.app/Ravyn%20Lenae%20-%20Love%20Me%20Not.mp3"),
        Song("2002", "Anne-Marie", "https://modern-pink-3czicetwms.edgeone.app/Anne-Marie%20-%202002%20(Mix%20Lyrics)%20Ellie%20Goulding,%20Meghan%20Trainor,%20Seafret%20[rvnkD_J_4lE].mp3"),
        Song("Byahe", "John Roa", "https://modern-pink-3czicetwms.edgeone.app/Byahe%20-%20Jroa%20(Lyrics).mp3"),
        Song("Angel Numbers/Ten Toes", "Chris Brown", "https://modern-pink-3czicetwms.edgeone.app/Chris%20Brown%20-%20Angel%20Numbers%20%20Ten%20Toes%20(Lyrics).mp3"),
        Song("To The Bone", "Pamungkas", "https://modern-pink-3czicetwms.edgeone.app/Pamungkas%20-%20To%20The%20Bone%20(Official%20Music%20Video).mp3")
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSongInteractionListener")
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

        val listView = view.findViewById<ListView>(R.id.songsListView)
        listView.adapter = SongAdapter(requireContext(), songList)
        listView.setOnItemClickListener { _, _, position, _ ->
            listener.onSongSelected(songList[position], position)
        }
    }
}
