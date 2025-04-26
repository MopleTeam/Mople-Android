package com.moim.core.common.util

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.moim.core.common.consts.DEEP_LINK_FOR_MEET
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MoimNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    init {
        createChannelGroup()
        createChannels()
    }

    private fun createChannelGroup() {
        val channelGroup = NotificationChannelGroup(NOTIFICATION_GROUP_ID, NOTIFICATION_NAME)
        notificationManager.createNotificationChannelGroup(channelGroup)
    }

    private fun createChannels() {
        val inviteChannel = NotificationChannel(CHANNEL_ID, NOTIFICATION_DESCRIPTION, NotificationManager.IMPORTANCE_HIGH)
            .apply {
                group = NOTIFICATION_GROUP_ID
                description = NOTIFICATION_DESCRIPTION
                lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
                setShowBadge(true)
                enableVibration(true)
            }

        notificationManager.createNotificationChannels(listOf(inviteChannel))
    }

    fun createNotificationBuilder(channelId: String = CHANNEL_ID): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSilent(true)
            .setAutoCancel(true)
    }

    fun getNotificationContentIntent(requestCode: Int, query: Map<String, String>): PendingIntent {
        val deepLink = DEEP_LINK_FOR_MEET.toUri()
            .buildUpon()
            .apply { query.map { appendQueryParameter(it.key, it.value) } }
            .build()
        val contentIntent = Intent(Intent.ACTION_VIEW, deepLink)
        val pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        return PendingIntent.getActivity(context, requestCode, contentIntent, pendingIntentFlag)
    }

    fun notify(id: Int, builder: NotificationCompat.Builder) = notificationManager.notify(id, builder.build())

    companion object {
        private const val NOTIFICATION_NAME = "mople"
        private const val NOTIFICATION_DESCRIPTION = "초대 알림"
        const val NOTIFICATION_GROUP_ID = "mople_notification_group"
        const val CHANNEL_ID = "moim-channel"
    }
}