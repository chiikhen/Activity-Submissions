package com.example.exercise_2_music_player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Music(
    val title: String,
    val url: String
)

class MusicAdapter(
    private val musicList: List<Music>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songTitle: TextView = itemView.findViewById(R.id.songItemTitle)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_list_item, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val music = musicList[position]
        holder.songTitle.text = music.title
    }

    override fun getItemCount(): Int = musicList.size
}

class MusicListFragment : Fragment() {

    interface OnMusicInteractionListener {
        fun onMusicSelected(music: Music, position: Int)
        fun onNextRequested()
        fun onPreviousRequested()
    }

    private var listener: OnMusicInteractionListener? = null
    private var musicList: List<Music> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMusicInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnMusicInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.musicRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = MusicAdapter(musicList) { position ->
            listener?.onMusicSelected(musicList[position], position)
        }
    }

    fun setMusicList(list: List<Music>) {
        musicList = list
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
