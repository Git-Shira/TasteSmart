package com.example.mypostsapp.ui.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypostsapp.DataBaseManager
import com.example.mypostsapp.entities.Post
import com.example.mypostsapp.entities.User
import com.example.mypostsapp.room.RoomManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostsViewModel : ViewModel() {

    private val _posts = ArrayList<Post>()
    val posts: MutableLiveData<ArrayList<Post>> = MutableLiveData(_posts)

    private val _user = MutableLiveData<User>().apply {
        value = User()
    }

    val currentUser: LiveData<User> = _user


    init {
        DataBaseManager.fetchPosts(viewModelScope, _posts) {
            postList()
        }

        DataBaseManager.getCurrentUser(FirebaseAuth.getInstance().uid!!) {
            if (it.isSuccessful) {
                _user.value = it.result.toObject(User::class.java)
                _user.postValue(_user.value)
                viewModelScope.launch(Dispatchers.IO) {
                    _user.value?.let {
                        RoomManager.database.userDao().insertUser(it)
                    }
                }
            }
        }
    }

    fun addPost(post: Post, user: User) {
        _posts.add(0, post)
        _user.value = user
        posts.postValue(posts.value)
    }

    fun updatePost(position: Int, post: Post, user: User) {
        _posts.set(position, post)
        _user.value = user
        postList()
    }

    private fun postList() {
        _posts.sortByDescending { it.created }
        posts.postValue(posts.value)
        viewModelScope.launch(Dispatchers.IO) {
            posts.value?.forEach {
                RoomManager.database.postDao().insertPost(it)
            }
        }
    }

    fun onLikeClicked(position: Int) {
        val selectedPost = _posts[position]
        selectedPost.likeUserIds?.firstOrNull {
            currentUser.value?.uid == it
        }?.let {
            selectedPost.likeUserIds?.remove(it)
            posts.postValue(_posts)
        } ?: run {
            selectedPost.likeUserIds?.add(currentUser.value?.uid!!) ?: run {
                selectedPost.likeUserIds = arrayListOf()
                selectedPost.likeUserIds?.add(currentUser.value?.uid!!)
            }
            posts.postValue(_posts)
        }

        DataBaseManager.savePost(selectedPost)
    }

    fun onDeleteClicked(position: Int) {
        val selectedPost = _posts[position]
        currentUser.value?.posts?.firstOrNull { it.uid == selectedPost.uid }?.let {
            currentUser.value?.posts?.remove(it)
        }
        _posts.remove(selectedPost)
        posts.postValue(_posts)
        DataBaseManager.deletePost(selectedPost, currentUser.value)
        viewModelScope.launch(Dispatchers.IO) {
            RoomManager.database.postDao().deletePost(selectedPost)
            currentUser.value?.let {
                RoomManager.database.userDao().insertUser(it)
            }
        }




    }


}