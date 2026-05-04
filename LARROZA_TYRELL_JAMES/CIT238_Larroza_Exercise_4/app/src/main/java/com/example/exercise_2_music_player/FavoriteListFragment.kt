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
import androidx.fragment.app.activityViewModels

class FavoriteListFragment : Fragment() {

    interface OnSongSelectedListener {
        fun onSongSelected(song: String)
    }

    private lateinit var listener: OnSongSelectedListener
    private val favoritesViewModel: FavoritesList by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as? OnSongSelectedListener
            ?: context as OnSongSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites_list, container, false)
        val listView = view.findViewById<ListView>(R.id.favoritesListView)
        val emptyText = view.findViewById<TextView>(R.id.emptyText)

        favoritesViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            if (favorites.isEmpty()) {
                emptyText.visibility = View.VISIBLE
                listView.visibility = View.GONE
            } else {
                emptyText.visibility = View.GONE
                listView.visibility = View.VISIBLE
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    favorites.map { it.substringBefore(" -") }
                )
                listView.adapter = adapter
                listView.setOnItemClickListener { _, _, position, _ ->
                    listener.onSongSelected(favorites[position])
                }
            }
        }
        return view
    }
}