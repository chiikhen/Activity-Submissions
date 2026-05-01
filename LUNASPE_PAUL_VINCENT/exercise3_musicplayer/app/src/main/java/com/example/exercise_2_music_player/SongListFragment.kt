package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class SongListFragment : Fragment() {

    private var listener: OnSongSelectedListener? = null

    private val songs = listOf(
        "The Nights - https://dn710000.ca.archive.org/0/items/01-avicii-the-nights-audio/01%20-%20Avicii%20-%20The%20Nights%20%28Audio%29.mp3",
        "Clarity - https://dn720306.ca.archive.org/0/items/clarity-by-zedd-ft.-foxes-lyrics-official-2026614/Clarity-By-Zedd-ft.-Foxes-Lyrics-Official_2026614.mp3",
        "Don't you worry child - https://dn721601.ca.archive.org/0/items/swedish-house-mafia-ft.-john-martin-dont-you-worry-child-official-video/Swedish%20House%20Mafia%20ft.%20John%20Martin%20-%20Don%27t%20You%20Worry%20Child%20%28Official%20Video%29.mp3"
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSongSelectedListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val songsListView = view.findViewById<ListView>(R.id.songsListView)
        val adapter = SongAdapter(requireContext(), songs)
        songsListView.adapter = adapter

        songsListView.setOnItemClickListener { _, _, position, _ ->
            listener?.onSongSelected(songs[position])
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private class SongAdapter(context: Context, private val songs: List<String>) :
        ArrayAdapter<String>(context, R.layout.song_list_item, songs) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.song_list_item, parent, false)

            val songData = songs[position]
            val songName = songData.substringBefore(" - ")

            view.findViewById<TextView>(android.R.id.text1).text = songName
            view.findViewById<TextView>(android.R.id.text2).text = "Streaming • Online"

            return view
        }
    }
}
