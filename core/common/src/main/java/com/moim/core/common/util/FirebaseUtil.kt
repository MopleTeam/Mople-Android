package com.moim.core.common.util

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

object FirebaseUtil {

    suspend fun getFirebaseMessageToken(): String? = suspendCancellableCoroutine { cancelCoroutine ->
        Firebase.messaging.token
            .addOnSuccessListener { cancelCoroutine.resume(it) }
            .addOnFailureListener {
                Timber.e("getFirebaseMessageToken exception message [$it]")
                cancelCoroutine.resume(null)
            }
    }
}