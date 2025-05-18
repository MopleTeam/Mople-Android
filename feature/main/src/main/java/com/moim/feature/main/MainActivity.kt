package com.moim.feature.main

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.AnalyticsHelper
import com.moim.core.analytics.LocalAnalyticsHelper
import com.moim.core.common.consts.INTRO_ACTIVITY_NAME
import com.moim.core.common.consts.KEY_INVITE_CODE
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.main.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        setContent {
            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
            ) {
                MoimTheme {
                    val meetingId by viewModel.meetingId.collectAsStateWithLifecycle()
                    val planId by viewModel.planId.collectAsStateWithLifecycle()
                    val reviewId by viewModel.reviewId.collectAsStateWithLifecycle()

                    MainScreen(
                        meetingId = meetingId,
                        planId = planId,
                        reviewId = reviewId,
                        navigateToIntro = this::navigateToIntro
                    )
                }
            }
        }

        if (savedInstanceState == null) {
            joinMeeting(intent)
            getNotifyData(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        joinMeeting(intent)
        getNotifyData(intent)
    }

    private fun joinMeeting(intent: Intent) {
        val meetCode = intent.getStringExtra(KEY_INVITE_CODE) ?: return
        viewModel.setJoinMeeting(meetCode)
    }

    private fun getNotifyData(notifyIntent: Intent) {
        with(notifyIntent) {
            getStringExtra(NOTIFY_MEET_ID)?.let { viewModel.setMeetingId(it) }
            getStringExtra(NOTIFY_PLAN_ID)?.let { viewModel.setPlanId(it) }
            getStringExtra(NOTIFY_REVIEW_ID)?.let { viewModel.setReviewId(it) }
        }
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