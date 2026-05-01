package com.example.exercise_2_music_player

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exercise_2_music_player.databinding.ItemSongBinding

class MusicAdapter(
    private val onSongClick: (Song) -> Unit,
    private val onFavoriteClick: (Song) -> Unit
    //comment
) : ListAdapter<Song, MusicAdapter.MusicViewHolder>(SongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MusicViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.songTitle.text = song.title
            binding.favoriteButton.setImageResource(
                if (song.isFavorite) R.drawable.ic_star else R.drawable.ic_star
            )
            binding.favoriteButton.setColorFilter(
                if (song.isFavorite) Color.YELLOW else Color.LTGRAY
            )
            
            binding.root.setOnClickListener { onSongClick(song) }
            binding.favoriteButton.setOnClickListener { onFavoriteClick(song) }
        }
    }

    class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean = oldItem.url == newItem.url
        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean = oldItem == newItem
    }
}
