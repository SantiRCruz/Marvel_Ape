package com.example.mapes

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mapes.core.getBitmap
import com.example.mapes.core.networkInfo
import com.example.mapes.data.localdb.AppDatabase
import com.example.mapes.data.models.entities.character.CharacterEntity
import com.example.mapes.databinding.ActivityMainBinding
import com.example.mapes.core.Constants
import com.example.mapes.core.Result
import com.example.marvelapes.data.rest.RetrofitClient
import com.example.marvelapes.domain.characters.CharacterRepoImpl
import com.example.marvelapes.presentation.CharacterViewModel
import com.example.marvelapes.presentation.CharacterViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModelCharacter by viewModels<CharacterViewModel> {
        CharacterViewModelFactory(
            CharacterRepoImpl(
                RetrofitClient.webService,
                AppDatabase.getDatabase(applicationContext).CharacterDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.supportActionBar?.hide()

        startTimer()
        clicks()
    }

    private fun clicks() {
        binding.llAlert.setOnClickListener {
            binding.llAlert.visibility = View.GONE
            checkConnection()
        }
    }

    private fun startTimer() {
        binding.ivLogo.animate().alpha(0f).scaleX(0f).scaleY(0f).setDuration(0)
            .withEndAction {
                binding.ivLogo.animate().alpha(1f).scaleX(1f).scaleY(1f)
                    .setDuration(1300).withEndAction {
                        checkConnection()
                    }
            }
    }

    private fun checkConnection() {
        if (!networkInfo(applicationContext)) {
            alertMessage("No encontramos coneccion a internet para descargar la informacion\n\n Presione para volver a intentar ")
        } else {
            binding.progress.visibility = View.VISIBLE
            fetchCharacter()

        }
    }

    private fun fetchCharacter() {
        lifecycleScope.launchWhenCreated {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelCharacter.getCharacters(
                ).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            Log.e("fetchCharacter: ", it.data.data.results.toString())
                            sendDataToBd(it.data.data.results)
                        }
                        is Result.Failure -> {
                            Log.e("Error: ", it.exception.toString())
                        }
                    }
                }
            }
        }
    }

    private fun sendDataToBd(results: List<com.example.marvelapes.data.models.web.Result>) {
        val list = mutableListOf<CharacterEntity>()
        lifecycleScope.launch {
            results.forEach {
                val b = getBitmap(
                    applicationContext,
                    "${it.thumbnail.path}${Constants.IMAGE_PORTRAIT}${it.thumbnail.extension}"
                )
                var newByteArray: ByteArray? = null
                val byteArrayOutputStream = ByteArrayOutputStream()
                b.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

                val bitmap = BitmapFactory.decodeByteArray(
                    byteArrayOutputStream.toByteArray(),
                    0,
                    byteArrayOutputStream.size()
                )
                val resized = Bitmap.createScaledBitmap(
                    bitmap,
                    (bitmap.width * 0.8).toInt(),
                    (bitmap.height * 0.8).toInt(),
                    true
                )
                val stream = ByteArrayOutputStream()
                resized.compress(Bitmap.CompressFormat.JPEG, 50, stream)
                newByteArray = stream.toByteArray()

                if (newByteArray.size > 500000) {
                    Snackbar.make(
                        binding.root,
                        "Lo sentimos esta imagenes tiene un tamaño muy grande, debe seleccionar otra",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return@forEach
                }

                val base64 = Base64.encodeToString(newByteArray, Base64.DEFAULT)
                list.add(
                    CharacterEntity(
                        it.id,
                        it.name,
                        it.description,
                        it.modified,
                        base64
                    )
                )
            }
            var count = 0
            list.forEach {
                lifecycleScope.launchWhenCreated {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModelCharacter.saveCharacter(it).collect {
                            when (it) {
                                is Result.Loading -> {}
                                is Result.Success -> {
                                    count++
                                    if (count == list.size){
                                        val i = Intent(this@MainActivity,LoginActivity::class.java)
                                        startActivity(i)
                                        finish()
                                    }

                                }
                                is Result.Failure -> {
                                    Log.e("Error: ", it.exception.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun alertMessage(s: String) {
        binding.txtAlert.text = s
        binding.llAlert.animate().translationY(300f).alpha(0f).setDuration(0).withEndAction {
            binding.llAlert.visibility = View.VISIBLE
            binding.llAlert.animate().translationY(0f).alpha(1f).setDuration(600)
        }
    }

}