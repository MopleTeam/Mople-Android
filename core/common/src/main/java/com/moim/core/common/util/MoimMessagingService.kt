package com.moim.core.common.util

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
        val (notifyTitle, notifyBody) = remoteMessage.data[NOTIFY_TITLE] to remoteMessage.data[NOTIFY_BODY]
        val meetId = remoteMessage.data[NOTIFY_MEET_ID].toString()
        val query = mapOf(QUERY_CODE to meetId)

        val notificationBuilder = moimNotificationManager
            .createNotificationBuilder()
            .setSmallIcon(R.drawable.ic_logo_full)
            .setContentTitle(notifyTitle)
            .setContentText(notifyBody)
            .setContentIntent(moimNotificationManager.getNotificationContentIntent(REQUEST_CODE, query))

        moimNotificationManager.notify(0, notificationBuilder)
    }

    companion object {
        private const val REQUEST_CODE = 100
        private const val NOTIFY_TITLE = "title"
        private const val NOTIFY_BODY = "body"
        private const val NOTIFY_MEET_ID = "meetId"

        private const val QUERY_CODE = "code"
    }
}