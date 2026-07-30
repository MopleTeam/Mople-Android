package com.moim.core.messaging

import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moim.core.common.di.IoDispatcher
import com.moim.core.data.datasource.token.TokenRepository
import com.moim.core.designsystem.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MoimMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var moimNotificationManager: MoimNotificationManager

    @Inject
    lateinit var tokenRepository: TokenRepository

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    private val serviceScope by lazy { CoroutineScope(SupervisorJob() + ioDispatcher) }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        sendNotification(remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch { tokenRepository.onFcmTokenRefreshed(token) }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val (notifyTitle, notifyBody) = remoteMessage.notification?.title to remoteMessage.notification?.body
        val bundle =
            Bundle().apply {
                putString(NOTIFY_MEET_ID, remoteMessage.data[NOTIFY_MEET_ID])
                putString(NOTIFY_PLAN_ID, remoteMessage.data[NOTIFY_PLAN_ID])
                putString(NOTIFY_REVIEW_ID, remoteMessage.data[NOTIFY_REVIEW_ID])
            }
        val notificationBuilder =
            moimNotificationManager
                .createNotificationBuilder()
                .setSmallIcon(R.drawable.ic_logo_full_light)
                .setContentTitle(notifyTitle)
                .setContentText(notifyBody)
                .setContentIntent(moimNotificationManager.getNotificationContentIntent(REQUEST_CODE, bundle))

        moimNotificationManager.notify(0, notificationBuilder)
    }

    companion object {
        private const val REQUEST_CODE = 100
        private const val NOTIFY_MEET_ID = "meetId"
        private const val NOTIFY_PLAN_ID = "planId"
        private const val NOTIFY_REVIEW_ID = "reviewId"
    }
}
