package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class FavoritesFragment : Fragment(), PlayableSongList, FavoriteStateAware {
    private var listener: MusicPlayerInterface? = null
    private lateinit var favoritesListView: ListView
    private lateinit var songAdapter: SongAdapter
    private var favoriteSongPositions: List<Int> = emptyList()

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
    ): View {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesListView = view.findViewById(R.id.favoritesListView)
        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    override fun syncPlayingSong(absolutePosition: Int) {
        if (::songAdapter.isInitialized) {
            songAdapter.currentPlayingPosition = favoriteSongPositions.indexOf(absolutePosition)
        }
    }

    private fun loadFavorites() {
        val mainActivity = activity as? MainActivity ?: return
        val allSongs = mainActivity.getSongs()

        favoriteSongPositions = mainActivity.getFavoriteSongPositions()
        val favoriteSongs = favoriteSongPositions.mapNotNull { index ->
            allSongs.getOrNull(index)
        }

        songAdapter = SongAdapter(
            context = requireContext(),
            songs = favoriteSongs,
            showFavoriteButton = true,
            isFavorite = { position -> mainActivity.isFavorite(position) },
            onFavoriteClick = { position -> mainActivity.showFavoriteConfirmation(position) },
            onSongClick = { position -> listener?.onSongSelected(position) },
            positionMapper = { adapterPosition ->
                favoriteSongPositions.getOrNull(adapterPosition) ?: -1
            }
        )
        favoritesListView.adapter = songAdapter
        syncPlayingSong(mainActivity.getCurrentSongPosition())
    }

    override fun refreshFavoriteState() {
        loadFavorites()
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
