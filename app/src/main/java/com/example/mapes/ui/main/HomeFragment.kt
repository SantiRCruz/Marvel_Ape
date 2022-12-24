package com.example.mapes.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.mapes.R
import com.example.mapes.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)


    }

}