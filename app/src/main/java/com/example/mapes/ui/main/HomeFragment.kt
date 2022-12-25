package com.example.mapes.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.mapes.R
import com.example.mapes.core.decode64
import com.example.mapes.core.Result
import com.example.mapes.data.localdb.AppDatabase
import com.example.mapes.data.models.entities.character.CharacterEntity
import com.example.mapes.databinding.FragmentHomeBinding
import com.example.mapes.ui.main.adapter.CharactersAdapter
import com.example.marvelapes.core.Constants
import com.example.marvelapes.data.rest.RetrofitClient
import com.example.marvelapes.domain.characters.CharacterRepoImpl
import com.example.marvelapes.presentation.CharacterViewModel
import com.example.marvelapes.presentation.CharacterViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private var characterList = listOf<CharacterEntity>()

    private val viewModelCharacter by viewModels<CharacterViewModel> {
        CharacterViewModelFactory(
            CharacterRepoImpl(
                RetrofitClient.webService,
                AppDatabase.getDatabase(requireContext()).CharacterDao()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        fetchCharacters()
        setUserData()
        clicks()
    }

    private fun clicks() {
    }

    private fun setUserData() {
        val shared =
            requireActivity().getSharedPreferences(Constants.SHARED_USER, Context.MODE_PRIVATE)
        val photo = shared.getString("photo", "")
        val name = shared.getString("names", "")
        binding.ivPhoto.setImageBitmap(photo?.decode64())
    }

    private fun fetchCharacters() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelCharacter.getDbCharacters().collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            characterList = it.data
                            setUpCharacters(it.data)
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

    private fun setUpCharacters(list: List<CharacterEntity>) {
        binding.vpCharacters.adapter = CharactersAdapter(list)
        binding.vpCharacters.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        moveAuto(list)
    }

    private fun moveAuto(list: List<CharacterEntity>) {
        object : CountDownTimer(3000, 100) {
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                if (binding.vpCharacters.currentItem == list.size - 1) {
                    binding.vpCharacters.currentItem = 0
                    moveAuto(list)
                } else {
                    binding.vpCharacters.currentItem++
                    moveAuto(list)
                }
            }
        }.start()
    }

}