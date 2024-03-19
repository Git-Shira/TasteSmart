package com.example.mypostsapp

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

class AlertDialogUtils {

    companion object {
        fun showAlert(context: Context, title: String, message: String) {
            AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .show()
        }

        fun showAlertWithButtons(context: Context, title: String, message: String,
                      positiveListener: DialogInterface.OnClickListener, negativeListener: DialogInterface.OnClickListener) {
            AlertDialog.Builder(context)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton(R.string.yes) {dialog, x->
                    positiveListener.onClick(dialog, x)
                }
                .setNegativeButton(R.string.cancel){dialog, x->
                    negativeListener.onClick(dialog, x)
                }
                .show()
        }
    }
}