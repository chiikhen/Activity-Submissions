package com.example.exercise_2_music_player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class MyMusicFragment : Fragment() {

    private lateinit var songsListView: ListView
    private lateinit var songTitle: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var songsAdapter: BaseAdapter

    private var currentSongIndex = -1
    private var songs: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        songs = listOf(
            getString(R.string.music_song_one),
            getString(R.string.music_song_two),
            getString(R.string.music_song_three)
        )

        songsListView = view.findViewById(R.id.songsListView)
        songTitle = view.findViewById(R.id.songTitle)
        playPauseButton = view.findViewById(R.id.playPauseButton)

        val previousButton = view.findViewById<ImageButton>(R.id.previousButton)
        val nextButton = view.findViewById<ImageButton>(R.id.nextButton)

        songsAdapter = object : BaseAdapter() {
            override fun getCount(): Int = songs.size

            override fun getItem(position: Int): Any = songs[position]

            override fun getItemId(position: Int): Long = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val rowView = convertView ?: layoutInflater.inflate(R.layout.item_song_music, parent, false)

                val song = songs[position]
                val titleTextView = rowView.findViewById<TextView>(R.id.songNameText)
                val favoriteButton = rowView.findViewById<ImageButton>(R.id.favoriteButton)

                titleTextView.text = song.substringBefore(" - ")
                favoriteButton.setImageResource(
                    if (FavoriteSongsStore.isFavorite(song)) R.drawable.ic_favorite_24
                    else R.drawable.ic_favorite_border_24
                )

                favoriteButton.setOnClickListener {
                    FavoriteSongsStore.toggle(song)
                    songsAdapter.notifyDataSetChanged()
                }

                rowView.setOnClickListener {
                    currentSongIndex = position
                    loadSongByIndex(currentSongIndex)
                }

                return rowView
            }
        }

        songsListView.adapter = songsAdapter

        songTitle.text = getString(R.string.music_choose_prompt)
        updatePlayPauseIcon(isPlaying = false)

        songsListView.setOnItemClickListener { _, _, position, _ ->
            currentSongIndex = position
            loadSongByIndex(currentSongIndex)
        }

        playPauseButton.setOnClickListener {
            if (SharedMusicPlayer.currentSong == null && songs.isNotEmpty()) {
                currentSongIndex = if (currentSongIndex >= 0) currentSongIndex else 0
                loadSongByIndex(currentSongIndex)
            } else {
                SharedMusicPlayer.togglePlayPause(requireContext())
                updatePlayPauseIcon(isPlaying = SharedMusicPlayer.isPlaying())
                syncPlaybackUi()
            }
        }

        previousButton.setOnClickListener {
            if (songs.isEmpty()) return@setOnClickListener
            currentSongIndex = if (currentSongIndex <= 0) songs.lastIndex else currentSongIndex - 1
            loadSongByIndex(currentSongIndex)
        }

        nextButton.setOnClickListener {
            if (songs.isEmpty()) return@setOnClickListener
            currentSongIndex = if (currentSongIndex >= songs.lastIndex || currentSongIndex < 0) 0 else currentSongIndex + 1
            loadSongByIndex(currentSongIndex)
        }
    }

    private fun loadSongByIndex(index: Int) {
        if (index !in songs.indices) return

        val song = songs[index]
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

    override fun onPause() {
        super.onPause()
        updatePlayPauseIcon(isPlaying = SharedMusicPlayer.isPlaying())
    }

    override fun onResume() {
        super.onResume()
        songsAdapter.notifyDataSetChanged()
        syncPlaybackUi()
        updatePlayPauseIcon(isPlaying = SharedMusicPlayer.isPlaying())
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
