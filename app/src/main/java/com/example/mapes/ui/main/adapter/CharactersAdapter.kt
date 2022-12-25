package com.example.mapes.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.mapes.core.decode64
import com.example.mapes.data.models.entities.character.CharacterEntity
import com.example.mapes.databinding.ItemCharacterBinding
import com.example.mapes.ui.main.HomeFragment
import com.example.mapes.ui.main.HomeFragmentDirections

class CharactersAdapter(
    private val list: List<CharacterEntity>,
) :
    RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        val binding =
            ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharactersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class CharactersViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CharacterEntity) {
            binding.ivPhoto.setImageBitmap(item.image.decode64())
            binding.tvDate.text = item.modified
            binding.tvTitle.text = item.name
            binding.tvDescription.text = item.description
            binding.btnContinue.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(item)
                Navigation.findNavController(binding.root).navigate(action)
            }
        }
    }
}