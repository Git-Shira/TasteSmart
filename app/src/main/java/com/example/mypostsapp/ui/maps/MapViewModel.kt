package com.example.mypostsapp.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypostsapp.DataBaseManager
import com.example.mypostsapp.entities.Post
import com.example.mypostsapp.room.RoomManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val posts: ArrayList<Post> = arrayListOf()
    val postsLD: MutableLiveData<ArrayList<Post>> = MutableLiveData()

    init {
        DataBaseManager.fetchPosts(viewModelScope, true, posts) {saveToRoom ->
            postsLD.postValue(posts)
            if (saveToRoom) {
                viewModelScope.launch(Dispatchers.IO) {
                    RoomManager.database.postDao().clearPostsTable()
                    posts.forEach {
                        RoomManager.database.postDao().insertPost(it)
                    }
                }
            }

        }
    }
}