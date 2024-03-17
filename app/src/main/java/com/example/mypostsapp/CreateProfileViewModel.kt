package com.example.mypostsapp

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mypostsapp.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class CreateProfileViewModel : ViewModel() {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val createSuccess : MutableLiveData<Unit> = MutableLiveData()
    val onError : MutableLiveData<String> = MutableLiveData()
    val currentUser: MutableLiveData<User> = MutableLiveData()


    init {
        DataBaseManager.getCurrentUser(FirebaseAuth.getInstance().uid!!) {
            if (it.isSuccessful) {
                currentUser.value = it.result.toObject(User::class.java)
                currentUser.postValue(currentUser.value)
            }

        }
    }
    fun createProfile(uid: String, fullName: String, imageBitmap: Bitmap?) {

        imageBitmap?.let {
            // the image name should be the user uuid - uniqe
            val imageRef: StorageReference = storage.reference.child("images/" + String + ".jpg")
            val baos = ByteArrayOutputStream()
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()
            // Upload file to Firebase Storage
            // Upload file to Firebase Storage
            val uploadTask = imageRef.putBytes(imageData)
            uploadTask.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                imageRef.downloadUrl.addOnSuccessListener { uri: Uri ->
                    saveUser(uid, fullName, uri.toString())
                }
            }.addOnFailureListener { e: Exception ->
                // Handle failed upload
                Log.e("TAG", "Upload failed: " + e.message)
            }
        } ?: run {
            saveUser(uid, fullName, currentUser.value?.image)
        }

    }

    private fun saveUser(uid: String, fullName: String, image: String?) {
        val user = User(uid, fullName, FirebaseAuth.getInstance().currentUser?.email, image)
        DataBaseManager.createUser(user) {
            if (it.isSuccessful) {
                createSuccess.postValue(Unit)
            } else{
                onError.postValue(it.exception?.message ?: "")
            }
        }
    }
}