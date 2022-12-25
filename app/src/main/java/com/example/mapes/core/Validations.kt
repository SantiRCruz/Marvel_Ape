package com.example.mapes.core

import androidx.core.util.PatternsCompat
import java.util.regex.Pattern

object Validations {

    fun validateEmail(s: String?): Boolean {
        return if (s.isNullOrEmpty()) {
            false
        } else PatternsCompat.EMAIL_ADDRESS.matcher(s).matches()
    }

    fun validateEmpty(s: String?): Boolean {
        return !s.isNullOrEmpty()
    }

}