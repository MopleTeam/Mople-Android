package com.moim.core.common.view

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showToast(context: Context, @StringRes messageRes: Int) {
    Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
}