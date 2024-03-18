package com.example.mypostsapp.ui.Login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    val onSignUpSuccess: MutableLiveData<Unit> = MutableLiveData()
    val onSignInSuccess: MutableLiveData<Unit> = MutableLiveData()
    val onError: MutableLiveData<String> = MutableLiveData()


    init {
        if (FirebaseAuth.getInstance().currentUser != null) {
            onSignInSuccess.postValue(Unit)
        }
    }

    fun onSignUpClicked(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignUpSuccess.postValue(Unit)
            }else {
                onError.postValue(task.exception?.message)
            }
        }
    }

    fun onSignInClicked(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignInSuccess.postValue(Unit)
            }else {
                onError.postValue(task.exception?.message ?: "")
            }
        }
    }
}