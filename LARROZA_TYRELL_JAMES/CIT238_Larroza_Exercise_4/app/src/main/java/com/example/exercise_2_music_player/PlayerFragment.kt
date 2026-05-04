package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerFragment : Fragment() {
    private lateinit var player: ExoPlayer
    private lateinit var title: TextView
    private lateinit var favorite: Button
    private var currentSong: String = ""
    private val favoritesViewModel: FavoritesList by activityViewModels()

    interface PlayerControls {
        fun onNextSong()
        fun onPreviousSong()
    }
    private lateinit var controls: PlayerControls

    override fun onAttach(context: Context) {
        super.onAttach(context)
        controls = parentFragment as? PlayerControls
            ?: context as PlayerControls
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_player, container, false)

        title = view.findViewById(R.id.songTitleFragment)

        val play = view.findViewById<Button>(R.id.playButton)
        val pause = view.findViewById<Button>(R.id.pauseButton)
        val stop = view.findViewById<Button>(R.id.stopButton)
        val next = view.findViewById<Button>(R.id.nextButton)
        val previous = view.findViewById<Button>(R.id.prevButton)
        favorite = view.findViewById(R.id.favoriteButton)

        player = ExoPlayer.Builder(requireContext()).build()

        play.setOnClickListener { player.play() }
        pause.setOnClickListener { player.pause() }
        stop.setOnClickListener {
            player.stop()
            player.clearMediaItems()
        }

        next.setOnClickListener {
            controls.onNextSong()
        }

        previous.setOnClickListener {
            controls.onPreviousSong()
        }

        favorite.setOnClickListener {
            if (currentSong.isNotEmpty()) {
                favoritesViewModel.toggleFavorite(currentSong)
                updateFavoriteButton()
            }
        }

        return view
    }

    fun updateSong(song: String) {
        currentSong = song
        val songName = song.substringBefore(" -")
        val url = song.substringAfter(" - ")
        title.text = songName
        updateFavoriteButton()

        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    private fun updateFavoriteButton() {
        val isFav = favoritesViewModel.isFavorite(currentSong)
        favorite.text = if (isFav) "Unfavorite" else "Favorite"
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}