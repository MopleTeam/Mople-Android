package com.moim.core.ui.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.ShareCompat
import com.moim.core.designsystem.R
import timber.log.Timber

@SuppressLint("QueryPermissionsNeeded")
fun Context.externalShareForUrl(url: String) {
    try {
        val shareIntent = ShareCompat.IntentBuilder(this)
            .setChooserTitle(getString(R.string.app_name))
            .setType("text/*")
            .setText(url)
            .createChooserIntent()
        startActivity(shareIntent)
    } catch (e: Exception) {
        Timber.e("[externalShareForUrl] message: ${e.message}")
    }
}