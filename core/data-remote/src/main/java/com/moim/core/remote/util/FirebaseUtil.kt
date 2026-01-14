package com.moim.core.remote.util

import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

object FirebaseUtil {
    suspend fun getFirebaseMessageToken(): String? =
        suspendCancellableCoroutine { cancelCoroutine ->
            Firebase.messaging.token
                .addOnSuccessListener { cancelCoroutine.resume(it) }
                .addOnFailureListener {
                    Timber.e("getFirebaseMessageToken exception message [$it]")
                    cancelCoroutine.resume(null)
                }
        }
}
