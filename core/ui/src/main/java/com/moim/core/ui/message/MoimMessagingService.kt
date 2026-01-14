package com.moim.core.ui.message

import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moim.core.designsystem.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MoimMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var moimNotificationManager: MoimNotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("[onMessageReceived] notification : ${remoteMessage.notification?.title}, ${remoteMessage.notification?.body}")
        Timber.d("[onMessageReceived] remoteMessageData : ${remoteMessage.data}")
        sendNotification(remoteMessage)
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
                .setSmallIcon(R.drawable.ic_logo_full)
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
