package com.example.exercise_2_music_player

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesAdapter(
    private val favoritesList: List<Music>,
    private val onItemClick: (Int) -> Unit,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songTitle: TextView = itemView.findViewById(R.id.favoriteItemTitle)
        val songArtist: TextView = itemView.findViewById(R.id.favoriteItemArtist)
        val removeButton: ImageView = itemView.findViewById(R.id.favoriteItemRemove)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
            removeButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRemoveClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_list_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val music = favoritesList[position]
        holder.songTitle.text = music.title
        holder.songArtist.text = music.artist

        // Heart is always filled and red for favorites
        ImageViewCompat.setImageTintList(
            holder.removeButton,
            ColorStateList.valueOf(holder.itemView.context.getColor(R.color.apple_music_red))
        )
    }

    override fun getItemCount(): Int = favoritesList.size
}

class FavoritesFragment : Fragment() {

    private var listener: MusicListFragment.OnMusicInteractionListener? = null
    private var favoritesList: List<Music> = emptyList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = (parentFragment as? MusicListFragment.OnMusicInteractionListener)
            ?: (context as? MusicListFragment.OnMusicInteractionListener)
            ?: throw RuntimeException("Host must implement OnMusicInteractionListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.favoritesRecyclerView)
        val emptyContainer = view.findViewById<LinearLayout>(R.id.emptyFavoritesContainer)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (favoritesList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyContainer.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyContainer.visibility = View.GONE
        }

        recyclerView.adapter = FavoritesAdapter(favoritesList, { position ->
            listener?.onMusicSelected(favoritesList[position], position)
        }, { position ->
            // Remove from favorites via the parent listener
            listener?.onFavoriteToggled(favoritesList[position])
        })
    }

    fun setFavoritesList(list: List<Music>) {
        favoritesList = list
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
