package com.moim.feature.main

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.moim.core.analytics.AnalyticsHelper
import com.moim.core.analytics.LocalAnalyticsHelper
import com.moim.core.common.consts.INTRO_ACTIVITY_NAME
import com.moim.core.common.consts.KEY_INVITE_CODE
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.main.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var meetingRepository: MeetingRepository

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    private val meetingId = MutableStateFlow<String?>(null)
    private val planId = MutableStateFlow<String?>(null)
    private val reviewId = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        setContent {
            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
            ) {
                MoimTheme {
                    val meetingId by meetingId.collectAsStateWithLifecycle()
                    val planId by planId.collectAsStateWithLifecycle()
                    val reviewId by reviewId.collectAsStateWithLifecycle()

                    MainScreen(
                        meetingId = meetingId,
                        planId = planId,
                        reviewId = reviewId,
                        navigateToIntro = this::navigateToIntro
                    )
                }
            }
        }

        joinMeeting(intent)
        getNotifyData(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        joinMeeting(intent)
        getNotifyData(intent)
    }

    private fun joinMeeting(intent: Intent) {
        val meetCode = intent.getStringExtra(KEY_INVITE_CODE) ?: return
        lifecycleScope.launch {
            runCatching {
                val meeting = meetingRepository.joinMeeting(meetCode).first()
                meetingId.update { meeting.id }
            }
        }
    }

    private fun getNotifyData(notifyIntent: Intent) {
        meetingId.update { notifyIntent.getStringExtra(NOTIFY_MEET_ID) }
        planId.update { notifyIntent.getStringExtra(NOTIFY_PLAN_ID) }
        reviewId.update { notifyIntent.getStringExtra(NOTIFY_REVIEW_ID) }
    }

    companion object {
        private const val NOTIFY_MEET_ID = "meetId"
        private const val NOTIFY_PLAN_ID = "planId"
        private const val NOTIFY_REVIEW_ID = "reviewId"
    }
}

private fun Activity.navigateToIntro() {
    val intent = Intent(this, Class.forName(INTRO_ACTIVITY_NAME))
    finish()
    startActivity(intent)
}