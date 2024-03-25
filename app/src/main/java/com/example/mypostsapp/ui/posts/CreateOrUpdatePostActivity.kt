package com.example.mypostsapp.ui.posts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mypostsapp.R
import com.example.mypostsapp.entities.Post
import com.example.mypostsapp.entities.User
import com.example.mypostsapp.ui.main.CreateOrUpdatePostFragment

class CreateOrUpdatePostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_update_post2)
        val position: Int? = intent.getIntExtra("position", -1)
        val post : Post? = intent.getSerializableExtra("post") as? Post
        val user : User? = intent.getSerializableExtra("user") as? User

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CreateOrUpdatePostFragment.newInstance(if (position == -1) null else position, post,user))
                .commitNow()
        }
    }
}