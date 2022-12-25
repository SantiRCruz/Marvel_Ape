package com.example.mapes.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import com.example.mapes.core.Result
import androidx.core.util.PatternsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mapes.MenuActivity
import com.example.mapes.R
import com.example.mapes.databinding.FragmentLogInBinding
import com.example.mapes.core.Constants
import com.example.mapes.data.localdb.AppDatabase
import com.example.marvelapes.data.models.entities.user.UserEntity
import com.example.marvelapes.domain.user.UserRepoImpl
import com.example.mapes.presentation.UserViewModel
import com.example.mapes.presentation.UserViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LogInFragment : Fragment(R.layout.fragment_log_in) {
        private lateinit var binding: FragmentLogInBinding

        private val viewModelUsers by viewModels<UserViewModel> {
                UserViewModelFactory(
                        UserRepoImpl(
                                AppDatabase.getDatabase(requireContext()).UserDao()
                        )
                )
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                binding = FragmentLogInBinding.bind(view)

                setAnimations()
                clicks()
        }

        private fun setAnimations() {
                binding.textView.startAnimation(
                        AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.sttb
                        )
                )
                binding.ivLogo.startAnimation(
                        AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.sttb
                        )
                )
                binding.cLSignIn.startAnimation(
                        AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.sbtt
                        )
                )
                binding.imageView.startAnimation(
                        AnimationUtils.loadAnimation(
                                requireContext(),
                                R.anim.smtm
                        )
                )
        }

        private fun clicks() {
                binding.tvRegister.setOnClickListener { findNavController().navigate(R.id.action_logInFragment_to_signUpFragment) }
                binding.btnLogin.setOnClickListener { validate() }
        }

        private fun validate() {
                val res = arrayOf(
                        validateEmpty(binding.tILPassword, binding.tIEPassword),
                        validateEmail(binding.tILEmail, binding.tIEEmail),
                )
                if (false in res)return

                sendSignIn()
        }

        private fun sendSignIn() {
                viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                viewModelUsers.getOneUser(binding.tIEEmail.text.toString()).collect {
                                        when (it) {
                                                is Result.Loading -> {}
                                                is Result.Success -> {
                                                        if (it.data != null) {
                                                                if (it.data.password == binding.tIEPassword.text.toString()) {
                                                                        saveUserData(it.data)
                                                                        val i = Intent(requireContext(), MenuActivity::class.java)
                                                                        startActivity(i)
                                                                        requireActivity().finish()
                                                                } else {
                                                                        Snackbar.make(
                                                                                binding.root,
                                                                                "La contraseña no coincide",
                                                                                Snackbar.LENGTH_SHORT
                                                                        ).show()
                                                                }
                                                        }else{
                                                                Snackbar.make(
                                                                        binding.root,
                                                                        "No existe ese usuario",
                                                                        Snackbar.LENGTH_SHORT
                                                                ).show()
                                                        }

                                                }
                                                is Result.Failure -> {
                                                        Log.e("sendUser: ", it.exception.message.toString())
                                                        Snackbar.make(
                                                                binding.root,
                                                                "Error al comunicarse con la bd",
                                                                Snackbar.LENGTH_SHORT
                                                        ).show()
                                                }
                                        }
                                }
                        }
                }
        }

        private fun saveUserData(usersEntity: UserEntity) {
                val shared = requireContext().getSharedPreferences(Constants.SHARED_USER, Context.MODE_PRIVATE)
                shared.edit().apply {
                        putInt("idUser", usersEntity.id_user)
                        putString("names", usersEntity.names)
                        putString("email", usersEntity.email)
                        putString("phone", usersEntity.phone)
                        putString("photo", usersEntity.photo)
                }.apply()
        }

        private fun validateEmail(tIL: TextInputLayout, tIE: TextInputEditText): Boolean {
                return if (tIE.text.isNullOrEmpty()) {
                        tIL.error = "Este campo es obligatorio"
                        false
                } else if(!PatternsCompat.EMAIL_ADDRESS.matcher(tIE.text.toString()).matches()){
                        tIL.error = "Este campo debe tener cuerpo de correo"
                        false
                }else{
                        tIL.error = null
                        true
                }
        }

        private fun validateEmpty(tIL: TextInputLayout, tIE: TextInputEditText): Boolean {
                return if (tIE.text.isNullOrEmpty()) {
                        tIL.error = "Este campo es obligatorio"
                        false
                } else {
                        tIL.error = null
                        true
                }
        }
}