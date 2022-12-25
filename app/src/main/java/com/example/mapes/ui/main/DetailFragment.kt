package com.example.mapes.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mapes.R
import com.example.mapes.core.Result
import com.example.mapes.core.decode64
import com.example.mapes.data.localdb.AppDatabase
import com.example.mapes.data.models.entities.favorite.FavoriteEntity
import com.example.mapes.databinding.FragmentDetailBinding
import com.example.mapes.databinding.ItemCharacterBinding
import com.example.mapes.domain.favorite.FavoriteRepoImpl
import com.example.mapes.presentation.FavoriteViewModel
import com.example.mapes.presentation.FavoriteViewModelFactory
import com.example.marvelapes.core.Constants
import com.example.marvelapes.data.rest.RetrofitClient
import com.example.marvelapes.domain.characters.CharacterRepoImpl
import com.example.marvelapes.presentation.CharacterViewModel
import com.example.marvelapes.presentation.CharacterViewModelFactory
import com.google.android.material.snackbar.Snackbar


class DetailFragment : Fragment(R.layout.fragment_detail) {

    private lateinit var binding: FragmentDetailBinding

    private var isLiked = false
    private var idUser = 0
    private val args by navArgs<DetailFragmentArgs>()

    private val viewModelFavorite by viewModels<FavoriteViewModel> {
        FavoriteViewModelFactory(
            FavoriteRepoImpl(
                AppDatabase.getDatabase(requireContext()).FavoriteDao()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDetailBinding.bind(view)

        setUserData()
        setData()
        clicks()
    }

    private fun clicks() {
        binding.ivLike.setOnClickListener {
            if (isLiked) {
                sendDeleteLike()
            } else {
                sendPostLike()
            }
        }
        binding.ivPrevious.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUserData() {
        val shared = requireActivity().getSharedPreferences(Constants.SHARED_USER, Context.MODE_PRIVATE)
        idUser = shared.getInt("idUser", 0)
    }

    private fun sendDeleteLike() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelFavorite.deleteFavorite(
                    idUser,
                    args.character.id
                ).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            isLiked = false
                            binding.ivLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)
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

    private fun sendPostLike() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelFavorite.saveFavorite(
                    FavoriteEntity(
                        0,
                        args.character.id,
                        idUser
                    )
                ).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            isLiked = true
                            binding.ivLike.setImageResource(R.drawable.ic_baseline_favorite_24)
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

    private fun setData() {
        binding.tvDescription.text = args.character.description
        binding.tvTitle.text = args.character.name
        binding.ivPhoto.setImageBitmap(args.character.image.decode64())
        fetchIsLiked()
    }

    private fun fetchIsLiked() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelFavorite.isLiked(
                    idUser,
                    args.character.id
                ).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            Log.e("fetchIsLiked: ",it.data.toString() )
                            if (it.data.isEmpty()){
                                isLiked = false
                                binding.ivLike.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                            }else{
                                isLiked = true
                                binding.ivLike.setImageResource(R.drawable.ic_baseline_favorite_24)
                            }
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
}