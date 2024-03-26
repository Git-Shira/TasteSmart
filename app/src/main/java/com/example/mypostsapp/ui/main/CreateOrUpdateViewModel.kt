package com.example.mypostsapp.ui.main

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mypostsapp.DataBaseManager
import com.example.mypostsapp.room.RoomManager
import com.example.mypostsapp.entities.Post
import com.example.mypostsapp.entities.PostLocation
import com.example.mypostsapp.entities.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

class CreateOrUpdateViewModel : ViewModel() {

    val onSuccessLD: MutableLiveData<Pair<Post, User?>> = MutableLiveData()
    val onErrorLD: MutableLiveData<String> = MutableLiveData()

    fun savePost(
        uid: String?,
        currentUser: User?,
        description: String,
        imageBitmap: Bitmap?,
        postImageUrl: String?,
        likeUserIds: ArrayList<String>?,
        location: PostLocation?
    ) {
        imageBitmap?.let {
            val imageRef: StorageReference = FirebaseStorage.getInstance().reference.child("images/" + String + ".jpg")
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()
            val uploadTask = imageRef.putBytes(imageData)
            uploadTask.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                imageRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                    savePostToDataBase(uid, uri.toString(), currentUser, description, likeUserIds, location)
                }
            }.addOnFailureListener { e: Exception ->
                // Handle failed upload
                Log.e("TAG", "Upload failed: " + e.message)
            }
        } ?: run {
            savePostToDataBase(uid, postImageUrl, currentUser, description, likeUserIds, location)
        }

    }

    private fun savePostToDataBase(
        uid: String?,
        postImageUrl: String?,
        user: User?,
        description: String,
        likeUserIds: ArrayList<String>?,
        location: PostLocation?
    ) {
        val _uid = uid ?: UUID.randomUUID().toString()
        val postData =
            Post(
                _uid, description, postImageUrl, user, likeUserIds,
                System.currentTimeMillis(), location
            )

        DataBaseManager.savePost(postData, user) {
            if (it.isSuccessful) {
                onSuccessLD.postValue(Pair(postData, user))
            } else {
                onErrorLD.postValue(it.exception?.message.toString())
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            RoomManager.database.postDao().insertPost(postData)
            user?.let { RoomManager.database.userDao().insertUser(user) }

        }
    }
}