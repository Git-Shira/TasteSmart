package com.example.mypostsapp.ui.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.mypostsapp.R
import com.example.mypostsapp.databinding.LoginActiviyLayoutBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActiviyLayoutBinding
    private val navController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_login_activity_navigation) as NavHostFragment
        navHostFragment.navController
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActiviyLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.login,
            R.id.createProfile
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)

    }
}