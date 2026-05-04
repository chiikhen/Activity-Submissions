package com.example.exercise_4_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class FavoritesFragment : Fragment() {

    private var player: ExoPlayer? = null
    private var currentlyPlayingIndex = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_favorites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.favoritesListView)

        val adapter = SongAdapter(
            requireContext(),
            FavoritesManager.favoriteSongs,
            FavoritesManager.favoriteRawIds
        )
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            if (currentlyPlayingIndex == position && player?.isPlaying == true) {
                player?.stop()
                player?.release()
                player = null
                currentlyPlayingIndex = -1
            } else {
                player?.stop()
                player?.release()

                val uri = android.net.Uri.parse(
                    "android.resource://${requireContext().packageName}/${FavoritesManager.favoriteRawIds[position]}"
                )
                player = ExoPlayer.Builder(requireContext()).build().also {
                    it.setMediaItem(MediaItem.fromUri(uri))
                    it.prepare()
                    it.play()
                }
                currentlyPlayingIndex = position
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
    }
}