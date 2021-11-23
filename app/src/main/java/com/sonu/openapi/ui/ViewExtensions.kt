package com.sonu.openapi.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.sonu.openapi.R

fun Context.displayToast(@StringRes res: Int) {
    Toast.makeText(this, Toast.LENGTH_SHORT, res).show()
}

fun Context.displayToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.displaySuccessDialog(message: String?) {
    MaterialDialog(this)
        .show {
            title(R.string.text_success)
            message(text = message)
            positiveButton { R.string.text_ok }
        }
}

fun Context.displayErrorDialog(message: String?) {

    MaterialDialog(this)
        .show {
            title(R.string.text_error)
            message(text = message)
            positiveButton { R.string.text_ok }
        }
}
