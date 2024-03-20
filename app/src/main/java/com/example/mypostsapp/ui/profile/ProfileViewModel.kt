package com.example.mypostsapp.ui.profile

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

class ProfileViewModel : ViewModel() {

    val userLD: MutableLiveData<User?> = MutableLiveData()
    private var user: User ?= null


    init {
        val uid = FirebaseAuth.getInstance().uid!!
        DataBaseManager.getCurrentUser(uid) {
            if (it.isSuccessful) {
                user = it.result.toObject(User::class.java)
                user?.let {
                    userLD.postValue(it)
                    viewModelScope.launch(Dispatchers.IO) {
                        RoomManager.database.userDao().insertUser(it)
                    }
                }

            }
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

}