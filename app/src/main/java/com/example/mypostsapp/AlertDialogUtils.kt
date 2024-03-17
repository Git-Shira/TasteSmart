package com.example.mypostsapp

import android.content.Context
import androidx.appcompat.app.AlertDialog

class AlertDialogUtils {

    companion object {
        fun showAlert(context: Context, title: String, message: String) {
            AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .show()
        }
    }
}