package com.example.mypostsapp.ui.createProfile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.mypostsapp.R
import com.example.mypostsapp.databinding.ActivityCreateProfileActiviyBinding
import com.example.mypostsapp.entities.User

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileActiviyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileActiviyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val user = intent.getSerializableExtra("user") as? User
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_profile) as NavHostFragment
        val navController = navHostFragment.navController
        navController
            .setGraph(R.navigation.profile_graph, Bundle().apply {
                putSerializable("user", user)
            })
    }
}