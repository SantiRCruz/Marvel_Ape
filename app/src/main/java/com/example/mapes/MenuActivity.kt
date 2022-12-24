package com.example.mapes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.mapes.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        observeDestination()
    }
    private fun observeDestination() {
        navController.addOnDestinationChangedListener{n,d,a ->
            when(d.id){
                R.id.qrFragment ->{binding.bottomNavigation.visibility = View.VISIBLE}
                R.id.mapFragment ->{binding.bottomNavigation.visibility = View.VISIBLE}
                R.id.homeFragment ->{binding.bottomNavigation.visibility = View.VISIBLE}
            }
        }
    }
}