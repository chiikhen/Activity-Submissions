package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

open class ListFragment : Fragment(), PlayableSongList, FavoriteStateAware {
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
        val mainActivity = activity as? MainActivity

        songAdapter = SongAdapter(
            context = requireContext(),
            songs = songs,
            showFavoriteButton = showFavoriteControls(),
            isFavorite = { position -> mainActivity?.isFavorite(position) == true },
            onFavoriteClick = { position -> onFavoriteClicked(position) },
            onSongClick = { position -> listener?.onSongSelected(position) }
        )
        songsListView.adapter = songAdapter
        syncPlayingSong((activity as? MainActivity)?.getCurrentSongPosition() ?: -1)
    }

    override fun onResume() {
        super.onResume()
        syncPlayingSong((activity as? MainActivity)?.getCurrentSongPosition() ?: -1)
        refreshFavoriteState()
    }

    /** Called by MainActivity to update which song is highlighted as playing */
    fun setPlayingPosition(position: Int) {
        if (::songAdapter.isInitialized) {
            songAdapter.currentPlayingPosition = position
        }
    }

    override fun syncPlayingSong(absolutePosition: Int) {
        setPlayingPosition(absolutePosition)
    }

    open fun showFavoriteControls(): Boolean = false

    open fun onFavoriteClicked(position: Int) {
        (activity as? MainActivity)?.showFavoriteConfirmation(position)
    }

    override fun refreshFavoriteState() {
        if (::songAdapter.isInitialized) {
            songAdapter.notifyDataSetChanged()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}

interface PlayableSongList {
    fun syncPlayingSong(absolutePosition: Int)
}

interface FavoriteStateAware {
    fun refreshFavoriteState()
}
