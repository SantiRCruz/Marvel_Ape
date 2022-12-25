package com.example.mapes.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapes.core.decode64
import com.example.mapes.data.models.entities.joins.FavoriteAndCharacter
import com.example.mapes.databinding.ItemFavoritesBinding

class FavoritesAdapter(
    private val list: List<FavoriteAndCharacter>,
    private val onClickDelete: (Int, FavoriteAndCharacter) -> Unit,
    private val onClickItem: (FavoriteAndCharacter) -> Unit
) :
    RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val binding =
            ItemFavoritesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(list[position], onClickDelete, onClickItem)
    }

    override fun getItemCount(): Int = list.size

    inner class FavoritesViewHolder(private val binding: ItemFavoritesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: FavoriteAndCharacter,
            onClickDelete: (Int, FavoriteAndCharacter) -> Unit,
            onClickItem: (FavoriteAndCharacter) -> Unit
        ) {

            binding.ivPhoto.setImageBitmap(item.image.decode64())
            binding.tvDate.text = item.modified
            binding.tvTitle.text = item.name
            binding.ivLike.setOnClickListener { onClickDelete(adapterPosition, item) }
            binding.root.setOnClickListener { onClickItem(item) }
        }
    }
}