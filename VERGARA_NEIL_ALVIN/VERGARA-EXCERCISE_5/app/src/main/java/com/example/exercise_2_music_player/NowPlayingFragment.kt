package com.example.exercise_2_music_player

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.exercise_2_music_player.R

class NowPlayingFragment : Fragment() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var favoriteButton: ImageButton
    private lateinit var waveform: WaveformView
    private lateinit var songList: ListView
    private lateinit var statusText: TextView
    private var currentSongIndex = 0

    private val songs = listOf(
        "Song 1 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "Song 2 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "Song 3 - https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )

    private val favorites = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_now_playing, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        waveform = view.findViewById(R.id.waveformView)
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        previousButton = view.findViewById(R.id.previousButton)
        nextButton = view.findViewById(R.id.nextButton)
        favoriteButton = view.findViewById(R.id.favoriteButton)
        statusText = view.findViewById(R.id.statusText)
        songList = view.findViewById(R.id.songList)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, songs)
        songList.adapter = adapter

        songList.setOnItemClickListener { _, _, position, _ ->
            currentSongIndex = position
            playSong(songs[position])
        }

        playButton.setOnClickListener { playSong(songs[currentSongIndex]) }
        pauseButton.setOnClickListener { pauseSong() }
        
        previousButton.setOnClickListener {
            if (currentSongIndex > 0) {
                currentSongIndex--
                playSong(songs[currentSongIndex])
            } else {
                currentSongIndex = songs.size - 1
                playSong(songs[currentSongIndex])
            }
        }

        nextButton.setOnClickListener {
            if (currentSongIndex < songs.size - 1) {
                currentSongIndex++
                playSong(songs[currentSongIndex])
            } else {
                currentSongIndex = 0
                playSong(songs[currentSongIndex])
            }
        }

        favoriteButton.setOnClickListener {
            val currentSong = songs[currentSongIndex]
            if (!favorites.contains(currentSong)) {
                favorites.add(currentSong)
                Toast.makeText(requireContext(), "Added to Favorites!", Toast.LENGTH_SHORT).show()
                favoriteButton.setColorFilter(0xFF1DB954.toInt()) // Spotify Green
            } else {
                favorites.remove(currentSong)
                Toast.makeText(requireContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show()
                favoriteButton.setColorFilter(0xFFFFFFFF.toInt()) // White
            }
        }
    }

    private fun playSong(song: String) {
        val parts = song.split(" - ")
        if (parts.size < 2) return

        val title = parts[0]
        val url = parts[1]

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                start()
                this@NowPlayingFragment.isPlaying = true
                waveform.setIsPlaying(true)
                statusText.text = "Playing: $title"
                
                // Update favorite button state for the new song
                if (favorites.contains(songs[currentSongIndex])) {
                    favoriteButton.setColorFilter(0xFF1DB954.toInt())
                } else {
                    favoriteButton.setColorFilter(0xFFFFFFFF.toInt())
                }
            }
            setOnErrorListener { _, _, _ -> false }
            prepareAsync()
        }
    }

    private fun pauseSong() {
        mediaPlayer?.pause()
        isPlaying = false
        waveform.setIsPlaying(false)
        statusText.text = "Paused"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
    }
}