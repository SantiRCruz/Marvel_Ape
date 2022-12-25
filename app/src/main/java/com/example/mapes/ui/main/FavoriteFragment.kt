package com.example.mapes.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.mapes.R
import com.example.mapes.core.Result
import com.example.mapes.core.decode64
import com.example.mapes.data.localdb.AppDatabase
import com.example.mapes.data.models.entities.joins.FavoriteAndCharacter
import com.example.mapes.databinding.DialogDetailBinding
import com.example.mapes.databinding.FragmentFavoriteBinding
import com.example.mapes.domain.favorite.FavoriteRepoImpl
import com.example.mapes.presentation.FavoriteViewModel
import com.example.mapes.presentation.FavoriteViewModelFactory
import com.example.mapes.ui.main.adapter.FavoritesAdapter
import com.example.mapes.core.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar


class FavoriteFragment : Fragment(R.layout.fragment_favorite) {
    private lateinit var binding: FragmentFavoriteBinding

    private lateinit var favoriteAdapter: FavoritesAdapter

    private var favoriteList = mutableListOf<FavoriteAndCharacter>()

    private val viewModelFavorite by viewModels<FavoriteViewModel> {
        FavoriteViewModelFactory(
            FavoriteRepoImpl(
                AppDatabase.getDatabase(requireContext()).FavoriteDao()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)


        setData()
    }

    private fun setData() {
        val shared =
            requireActivity().getSharedPreferences(Constants.SHARED_USER, Context.MODE_PRIVATE)
        val photo = shared.getString("photo", "")
        val idUser = shared.getInt("idUser", 0)
        binding.ivPhoto.setImageBitmap(photo?.decode64())
        fetchFavorites(idUser)

    }

    private fun fetchFavorites(idUser: Int) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelFavorite.getDbFavorites(idUser).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            favoriteList = it.data.toMutableList()
                            setUpFavorites(favoriteList)
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al obtener los datos",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "obtainAbilities: ${it.exception.message}")
                        }
                    }
                }
            }
        }

    }

    private fun setUpFavorites(data: List<FavoriteAndCharacter>) {
        favoriteAdapter = FavoritesAdapter(
            data,
            onClickDelete = { listPosition, item -> onDeleteFavorite(listPosition, item) },
            onClickItem = { item -> onClickItem(item) },
        )
        binding.rvFavorites.adapter = favoriteAdapter
        binding.rvFavorites.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
    }

    private fun onClickItem(item: FavoriteAndCharacter) {
        val dialog = DialogDetailBinding.inflate(LayoutInflater.from(requireContext()))
        val alert = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme).apply {
            setContentView(dialog.root)
        }

        dialog.tvTitle.text = item.name
        dialog.tvDescription.text = item.description
        dialog.ivClose.setOnClickListener { alert.dismiss() }

        alert.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alert.show()
    }

    private fun onDeleteFavorite(listPosition: Int, item: FavoriteAndCharacter) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelFavorite.deleteFavorite(
                    item.user_id,
                    item.character_id
                ).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            favoriteList.removeAt(listPosition)
                            favoriteAdapter.notifyItemRemoved(listPosition)
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al quitar el like",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "obtainAbilities: ${it.exception.message}")
                        }
                    }
                }
            }
        }
    }
}