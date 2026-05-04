package com.example.exercise_2_music_player

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Music(
    val title: String,
    val artist: String = "Unknown Artist",
    val url: String
)

class MusicAdapter(
    private val musicList: List<Music>,
    private var favoritedUrls: Set<String>,
    private val onItemClick: (Int) -> Unit,
    private val onFavoriteClick: (Int) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songTitle: TextView = itemView.findViewById(R.id.songItemTitle)
        val songArtist: TextView = itemView.findViewById(R.id.songItemArtist)
        val favoriteButton: ImageView = itemView.findViewById(R.id.songItemFavorite)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
            favoriteButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavoriteClick(position)
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
        holder.songArtist.text = music.artist

        val isFavorited = music.url in favoritedUrls
        if (isFavorited) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite)
            ImageViewCompat.setImageTintList(
                holder.favoriteButton,
                ColorStateList.valueOf(holder.itemView.context.getColor(R.color.apple_music_red))
            )
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
            ImageViewCompat.setImageTintList(
                holder.favoriteButton,
                ColorStateList.valueOf(holder.itemView.context.getColor(R.color.apple_music_secondary_text))
            )
        }
    }

    override fun getItemCount(): Int = musicList.size

    fun updateFavorites(newFavoritedUrls: Set<String>) {
        favoritedUrls = newFavoritedUrls
        notifyDataSetChanged()
    }
}

class MusicListFragment : Fragment() {

    interface OnMusicInteractionListener {
        fun onMusicSelected(music: Music, position: Int)
        fun onNextRequested()
        fun onPreviousRequested()
        fun onFavoriteToggled(music: Music) {}
    }

    private var listener: OnMusicInteractionListener? = null
    private var musicList: List<Music> = emptyList()
    private var favoritedUrls: Set<String> = emptySet()
    private var adapter: MusicAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = (parentFragment as? OnMusicInteractionListener)
            ?: (context as? OnMusicInteractionListener)
            ?: throw RuntimeException("Host must implement OnMusicInteractionListener")
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

        adapter = MusicAdapter(musicList, favoritedUrls, { position ->
            listener?.onMusicSelected(musicList[position], position)
        }, { position ->
            listener?.onFavoriteToggled(musicList[position])
        })
        recyclerView.adapter = adapter
    }

    fun setMusicList(list: List<Music>) {
        musicList = list
    }

    fun setFavoritedUrls(urls: Set<String>) {
        favoritedUrls = urls
        adapter?.updateFavorites(urls)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
