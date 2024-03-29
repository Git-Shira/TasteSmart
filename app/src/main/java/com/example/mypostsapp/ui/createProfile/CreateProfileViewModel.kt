package com.example.mypostsapp.ui.createProfile

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mypostsapp.DataBaseManager
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
    val currentUserLD: MutableLiveData<User> = MutableLiveData()
    private var currentUser : User ?= null


    fun createOrUpdateProfile(email: String, password: String?, fullName: String, imageBitmap: Bitmap?) {
        if (!password.isNullOrEmpty()) {
            createUserViaAuth(email, password){
                saveUserOnDataBase(FirebaseAuth.getInstance().uid ?: "", email, fullName, imageBitmap)
            }
        } else {
            saveUserOnDataBase(FirebaseAuth.getInstance().uid ?: "", email, fullName, imageBitmap)
        }

    }

    private fun saveUserOnDataBase(uid: String, email: String?, fullName: String, imageBitmap: Bitmap?) {
        imageBitmap?.let {
            // the image name should be the user uuid - uniqe
            val imageRef: StorageReference = storage.reference.child("images/$uid.jpg")
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
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
            saveUser(uid, fullName, currentUserLD.value?.image)
        }
    }

    private fun createUserViaAuth(email: String, password: String, onDone: () -> Any) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                onDone.invoke()
            } else {
                showError(it.exception?.message.toString())
            }
        }

    }

    private fun saveUser(uid: String, fullName: String, image: String?) {
        val user = User(uid, fullName, FirebaseAuth.getInstance().currentUser?.email, image)
        DataBaseManager.createUser(user) {
            if (it.isSuccessful) {
                createSuccess.postValue(Unit)
            } else {
                showError(it.exception?.message ?: "")
            }
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun showError(error: String) {
        onError.postValue(error)
    }

    fun setUser(user: User) {
        currentUser = user
        currentUserLD.postValue(user)
    }
}