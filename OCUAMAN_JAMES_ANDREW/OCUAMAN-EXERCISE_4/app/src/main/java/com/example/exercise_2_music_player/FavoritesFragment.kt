package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment() {

    private lateinit var favoritesListView: ListView
    private lateinit var songTitle: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var adapter: ArrayAdapter<String>
    private val favoriteSongTitles = mutableListOf<String>()

    private var currentSongIndex = -1
    private var favoriteSongs: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesListView = view.findViewById(R.id.favoritesListView)
        songTitle = view.findViewById(R.id.favoriteSongTitle)
        playPauseButton = view.findViewById(R.id.favoritePlayPauseButton)

        val previousButton = view.findViewById<ImageButton>(R.id.favoritePreviousButton)
        val nextButton = view.findViewById<ImageButton>(R.id.favoriteNextButton)

        adapter = ArrayAdapter(requireContext(), R.layout.item_song_favorite, R.id.songNameText, favoriteSongTitles)
        favoritesListView.adapter = adapter

        refreshFavorites()
        updatePlayPauseIcon(isPlaying = false)

        favoritesListView.setOnItemClickListener { _, _, position, _ ->
            currentSongIndex = position
            loadFavoriteSongByIndex(currentSongIndex)
        }

        playPauseButton.setOnClickListener {
            if (SharedMusicPlayer.currentSong == null && favoriteSongs.isNotEmpty()) {
                currentSongIndex = if (currentSongIndex >= 0) currentSongIndex else 0
                loadFavoriteSongByIndex(currentSongIndex)
            } else {
                SharedMusicPlayer.togglePlayPause(requireContext())
                updatePlayPauseIcon(isPlaying = SharedMusicPlayer.isPlaying())
                syncPlaybackUi()
            }
        }

        previousButton.setOnClickListener {
            if (favoriteSongs.isEmpty()) return@setOnClickListener
            currentSongIndex = if (currentSongIndex <= 0) favoriteSongs.lastIndex else currentSongIndex - 1
            loadFavoriteSongByIndex(currentSongIndex)
        }

        nextButton.setOnClickListener {
            if (favoriteSongs.isEmpty()) return@setOnClickListener
            currentSongIndex =
                if (currentSongIndex >= favoriteSongs.lastIndex || currentSongIndex < 0) 0 else currentSongIndex + 1
            loadFavoriteSongByIndex(currentSongIndex)
        }
    }

    private fun loadFavoriteSongByIndex(index: Int) {
        if (index !in favoriteSongs.indices) return

        val song = favoriteSongs[index]
        val title = song.substringBefore(" - ")

        songTitle.text = title

        SharedMusicPlayer.playSong(requireContext(), song)

        updatePlayPauseIcon(isPlaying = true)
    }

    private fun syncPlaybackUi() {
        val song = SharedMusicPlayer.currentSong
        if (song != null) {
            songTitle.text = song.substringBefore(" - ")
        }
    }

    private fun updatePlayPauseIcon(isPlaying: Boolean) {
        playPauseButton.setImageResource(
            if (isPlaying) R.drawable.ic_pause_24 else R.drawable.ic_play_arrow_24
        )
    }

    private fun refreshFavorites() {
        favoriteSongs = FavoriteSongsStore.getAll()
        favoriteSongTitles.clear()
        favoriteSongTitles.addAll(favoriteSongs.map { it.substringBefore(" - ") })
        adapter.notifyDataSetChanged()

        if (SharedMusicPlayer.currentSong != null) {
            songTitle.text = SharedMusicPlayer.currentSong!!.substringBefore(" - ")
        } else if (favoriteSongs.isEmpty()) {
            songTitle.text = getString(R.string.music_no_favorites)
            currentSongIndex = -1
        } else if (currentSongIndex !in favoriteSongs.indices) {
            songTitle.text = getString(R.string.music_choose_prompt)
            currentSongIndex = -1
        }
    }

    override fun onPause() {
        super.onPause()
        updatePlayPauseIcon(isPlaying = SharedMusicPlayer.isPlaying())
    }

    override fun onResume() {
        super.onResume()
        refreshFavorites()
        syncPlaybackUi()
        updatePlayPauseIcon(isPlaying = SharedMusicPlayer.isPlaying())
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
