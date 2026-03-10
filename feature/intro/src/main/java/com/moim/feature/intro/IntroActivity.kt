package com.moim.feature.intro

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.AnalyticsHelper
import com.moim.core.analytics.LocalAnalyticsHelper
import com.moim.core.common.consts.KEY_INVITE_CODE
import com.moim.core.common.consts.MAIN_ACTIVITY_NAME
import com.moim.core.common.model.Theme
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.intro.navigation.IntroNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IntroActivity : ComponentActivity() {
    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))

        setContent {
            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper,
            ) {
                val theme by userRepository.getTheme().collectAsStateWithLifecycle(Theme.SYSTEM)
                val isDarkTheme = shouldUseDarkTheme(theme)

                MoimTheme(
                    darkTheme = isDarkTheme,
                ) {
                    IntroNavHost {
                        navigateToMain()
                    }
                }
            }
        }
    }

    @Composable
    private fun shouldUseDarkTheme(theme: Theme): Boolean =
        when (theme) {
            Theme.SYSTEM -> isSystemInDarkTheme()
            Theme.DARK -> true
            Theme.LIGHT -> false
        }
}

private fun Activity.navigateToMain() {
    val bundles = intent.extras
    val inviteCode = intent.data?.getQueryParameter(KEY_INVITE_CODE).also { intent.data = null }
    val intent =
        Intent(this, Class.forName(MAIN_ACTIVITY_NAME)).apply {
            putExtra(KEY_INVITE_CODE, inviteCode)
            if (bundles != null) putExtras(bundles)
        }
    finish()
    startActivity(intent)
}
