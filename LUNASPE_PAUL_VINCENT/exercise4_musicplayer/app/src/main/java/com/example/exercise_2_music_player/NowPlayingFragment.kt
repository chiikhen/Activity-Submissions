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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class NowPlayingFragment : Fragment() {

    private val songs = listOf(
        "The Nights - https://dn710000.ca.archive.org/0/items/01-avicii-the-nights-audio/01%20-%20Avicii%20-%20The%20Nights%20%28Audio%29.mp3",
        "Clarity - https://dn720306.ca.archive.org/0/items/clarity-by-zedd-ft.-foxes-lyrics-official-2026614/Clarity-By-Zedd-ft.-Foxes-Lyrics-Official_2026614.mp3",
        "Don't you worry child - https://dn721601.ca.archive.org/0/items/swedish-house-mafia-ft.-john-martin-dont-you-worry-child-official-video/Swedish%20House%20Mafia%20ft.%20John%20Martin%20-%20Don%27t%20You%20Worry%20Child%20%28Official%20Video%29.mp3"
    )

    private var player: ExoPlayer? = null
    private var currentSongIndex = 0
    private var songUrl = ""
    private var songName = "Select a song"
    private var wasPlaying = false
    private var playbackPosition: Long = 0

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var songTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_now_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup song list
        val songsListView = view.findViewById<ListView>(R.id.songsListView)
        val adapter = SongAdapter(requireContext(), songs)
        songsListView.adapter = adapter

        songsListView.setOnItemClickListener { _, _, position, _ ->
            currentSongIndex = position
            loadSong(songs[position])
        }

        // Setup player controls
        playButton = view.findViewById(R.id.playButton)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        previousButton = view.findViewById(R.id.previousButton)
        nextButton = view.findViewById(R.id.nextButton)
        songTitle = view.findViewById(R.id.songTitle)

        initializePlayer()

        // Check if a song was passed from another fragment (My Music / Favorites)
        val songData = arguments?.getString("songData") ?: ""
        if (songData.isNotEmpty()) {
            val index = songs.indexOf(songData)
            if (index >= 0) currentSongIndex = index
            loadSong(songData)
        }

        playButton.setOnClickListener {
            player?.let {
                if (!it.isPlaying && it.playbackState == Player.STATE_IDLE) {
                    it.prepare()
                }
                wasPlaying = true
                it.play()
            }
        }

        pauseButton.setOnClickListener {
            wasPlaying = false
            player?.pause()
        }

        stopButton.setOnClickListener {
            wasPlaying = false
            playbackPosition = 0
            player?.stop()
            player?.seekTo(0)
        }

        previousButton.setOnClickListener {
            if (songs.isEmpty()) return@setOnClickListener
            currentSongIndex = if (currentSongIndex - 1 < 0) songs.size - 1 else currentSongIndex - 1
            loadSong(songs[currentSongIndex])
        }

        nextButton.setOnClickListener {
            if (songs.isEmpty()) return@setOnClickListener
            currentSongIndex = (currentSongIndex + 1) % songs.size
            loadSong(songs[currentSongIndex])
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    songTitle.text = "$songName - Playing"
                } else {
                    songTitle.text = "$songName - Paused"
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> songTitle.text = "$songName - Buffering..."
                    Player.STATE_READY -> songTitle.text = "$songName - Ready"
                    Player.STATE_IDLE -> songTitle.text = "$songName - Stopped"
                    Player.STATE_ENDED -> songTitle.text = "$songName - Ended"
                }
            }
        })
    }

    private fun loadSong(songData: String) {
        songUrl = songData.substringAfter(" - ")
        songName = songData.substringBefore(" - ")
        songTitle.text = songName

        wasPlaying = false
        playbackPosition = 0

        player?.stop()
        player?.clearMediaItems()

        val mediaItem = MediaItem.fromUri(songUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        wasPlaying = true
    }

    override fun onPause() {
        super.onPause()
        player?.let {
            playbackPosition = it.currentPosition
            wasPlaying = it.isPlaying
            it.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (wasPlaying) {
            player?.play()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
    }

    // Reuse the same SongAdapter from the original SongListFragment
    private class SongAdapter(context: android.content.Context, private val songs: List<String>) :
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
