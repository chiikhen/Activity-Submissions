package com.example.exercise_2_music_player

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment

class ManageSongFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var songTitle: TextView
    private lateinit var statusText: TextView

    private lateinit var listener: SongListFragment.OnSongSelectedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as SongListFragment.OnSongSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_manage_song, container, false)

        songTitle = view.findViewById(R.id.songTitleTextView)
        statusText = view.findViewById(R.id.statusTextView)

        val playButton = view.findViewById<ImageButton>(R.id.playButton)
        val pauseButton = view.findViewById<ImageButton>(R.id.pauseButton)
        val stopButton = view.findViewById<ImageButton>(R.id.stopButton)
        val nextButton = view.findViewById<ImageButton>(R.id.nextButton)
        val prevButton = view.findViewById<ImageButton>(R.id.prevButton)

        playButton.setOnClickListener {
            mediaPlayer?.start()
            statusText.text = "Playing"
        }

        pauseButton.setOnClickListener {
            mediaPlayer?.pause()
            statusText.text = "Paused"
        }

        stopButton.setOnClickListener {
            mediaPlayer?.stop()
            statusText.text = "Stopped"
        }

        nextButton.setOnClickListener {
            listener.onNextRequested()
        }

        prevButton.setOnClickListener {
            listener.onPreviousRequested()
        }

        return view
    }

    fun playSong(title: String, url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepare()
            start()
        }

        songTitle.text = title.substringBefore(" - ")
        statusText.text = "Playing"
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}