package com.example.mapes.ui.auth

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mapes.R
import com.example.mapes.data.localdb.AppDatabase
import com.example.mapes.databinding.DialogPhotoBinding
import com.example.mapes.databinding.FragmentSignUpBinding
import com.example.marvelapes.data.models.entities.user.UserEntity
import com.example.mapes.core.Result
import com.example.marvelapes.domain.user.UserRepoImpl
import com.example.mapes.presentation.UserViewModel
import com.example.mapes.presentation.UserViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.ByteArrayOutputStream


class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var binding: FragmentSignUpBinding

    private var bitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    private val viewModelUsers by viewModels<UserViewModel> {
        UserViewModelFactory(
            UserRepoImpl(
                AppDatabase.getDatabase(requireContext()).UserDao()
            )
        )
    }

    private val registerPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (false in it.values) {
                Snackbar.make(
                    binding.root,
                    "You must enable the permissions",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                permissions()
            }
        }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imageBitmap = it?.data?.extras?.get("data") as Bitmap
                bitmap = imageBitmap
                binding.ivPhoto.setImageBitmap(bitmap)
            }
        }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data?.data
            data?.let { uri->
                bitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        requireContext().contentResolver,
                        Uri.parse(uri.toString())
                    )
                )
                binding.ivPhoto.setImageBitmap(bitmap)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignUpBinding.bind(view)

        bitmap = BitmapFactory.decodeResource(requireContext().resources,R.drawable.miles)
        setAnimations()
        clicks()
    }

    private fun clicks() {
        binding.tvLogIn.setOnClickListener { findNavController().popBackStack() }
        binding.btnSignUp.setOnClickListener { validate() }
        binding.ivTakePhoto.setOnClickListener { permissions() }
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

        binding.cLSignUp.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.sbtt
            )
        )
    }

    private fun validate() {
        val res = arrayOf(
            validateEmpty(binding.tILNames, binding.tIEName),
            validateEmpty(binding.tILIdentification, binding.tIEIdentification),
            validateEmail(binding.tILEmail, binding.tIEEmail),
            validateEmpty(binding.tILPhone, binding.tIEPhone),
            validateEmpty(binding.tILPassword, binding.tIEPassword),
            validateEmpty(binding.tILRepeatPassword, binding.tIERepeatPassword),
        )
        if (false in res)return

        if (!validateSamePasswords()) return
        sendUser()
    }

    private fun sendUser() {
        var newByteArray: ByteArray? = null
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

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

        if (newByteArray.size>500000) {
            Snackbar.make(binding.root,"Lo sentimos esta imagenes tiene un tamaño muy grande, debe seleccionar otra",Snackbar.LENGTH_SHORT).show()
            return
        }

        val base64 = Base64.encodeToString(newByteArray, Base64.DEFAULT)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelUsers.saveUser(
                    UserEntity(
                        0,
                        binding.tIEName.text.toString(),
                        binding.tIEIdentification.text.toString(),
                        binding.tIEEmail.text.toString(),
                        binding.tIEPhone.text.toString(),
                        binding.tIEPassword.text.toString(),
                        base64
                    )
                ).collect {
                    when (it) {
                        is Result.Loading -> {
                        }
                        is Result.Success -> {
                            findNavController().popBackStack()
                            Snackbar.make(
                                binding.root,
                                "Se guardo correctamente",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al registrar un sitio",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "sendUser: ${it.exception}")
                        }
                    }
                }
            }
        }

    }

    private fun validateSamePasswords(): Boolean
    {
        return if (binding.tIEPassword.text.toString() != binding.tIERepeatPassword.text.toString()) {
            binding.tILPassword.error = "Las contraseñas no coinciden"
            binding.tILRepeatPassword.error = "Las contraseñas no coinciden"
            false
        } else {
            binding.tILPassword.error = null
            binding.tILRepeatPassword.error = null
            true
        }
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

    private fun permissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                dialogCameraOrGallery()
            }
            else -> {
                registerPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    private fun dialogCameraOrGallery() {
        val dialog = DialogPhotoBinding.inflate(LayoutInflater.from(requireContext()))
        val alert = AlertDialog.Builder(requireContext()).apply {
            setView(dialog.root)
        }.create()
        dialog.llCamera.setOnClickListener {
            pickFromCamera()
            alert.dismiss()
        }
        dialog.llGallery.setOnClickListener {
            pickFromGallery()
            alert.dismiss()
        }
        alert.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alert.show()
    }

    private fun pickFromCamera() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResult.launch(i)
    }

    private fun pickFromGallery() {
        val i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"
        galleryResult.launch(i)

    }

}