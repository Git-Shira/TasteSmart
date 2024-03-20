package com.example.mypostsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.mypostsapp.databinding.ContentPostsBinding
import com.example.mypostsapp.ui.posts.PostsScreenType

class PostsActivity : AppCompatActivity() {

    private lateinit var binding: ContentPostsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val type = intent.getSerializableExtra("type") as? PostsScreenType
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_posts) as NavHostFragment
        val navController = navHostFragment.navController
        navController
            .setGraph(R.navigation.posts_graph, Bundle().apply {
                putSerializable("type", type)
            })

    }

}