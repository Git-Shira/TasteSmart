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

    val onSuccess: MutableLiveData<Pair<Post, User?>> = MutableLiveData()
    val onError: MutableLiveData<String> = MutableLiveData()

    fun savePost(
        uid: String?,
        position: Int?,
        currentUser: User?,
        description: String,
        imageBitmap: Bitmap?,
        likeUserIds: ArrayList<String>?,
        location: PostLocation?
    ) {
        imageBitmap?.let {
            val imageRef: StorageReference = FirebaseStorage.getInstance().reference.child("images/" + String + ".jpg")
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()
            // Upload file to Firebase Storage
            // Upload file to Firebase Storage
            val uploadTask = imageRef.putBytes(imageData)
            uploadTask.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                imageRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                    savePostToDataBase(uid, position, currentUser, description, uri.toString(), likeUserIds, location)
                }
            }.addOnFailureListener { e: Exception ->
                // Handle failed upload
                Log.e("TAG", "Upload failed: " + e.message)
            }
        } ?: run {
            savePostToDataBase(uid, position, currentUser, description, null, likeUserIds, location)
        }

    }

    private fun savePostToDataBase(
        uid: String?,
        position: Int?,
        user: User?,
        description: String,
        uri: String?,
        likeUserIds: ArrayList<String>?,
        location: PostLocation?
    ) {
        val _uid = uid ?: UUID.randomUUID().toString()
        val postData =
            Post(
                _uid, description, uri, user, likeUserIds,
                System.currentTimeMillis(), location
            )

        DataBaseManager.savePost(postData, user) {
            if (it.isSuccessful) {
                onSuccess.postValue(Pair(postData, user))
            } else {
                onError.postValue(it.exception?.message.toString())
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            RoomManager.database.postDao().insertPost(postData)
            user?.let { RoomManager.database.userDao().insertUser(user) }

        }
    }
}