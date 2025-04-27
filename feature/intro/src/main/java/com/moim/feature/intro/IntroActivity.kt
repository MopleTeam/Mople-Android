package com.moim.feature.intro

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moim.core.common.consts.MAIN_ACTIVITY_NAME
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.intro.navigation.IntroNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hasFcmData = intent?.extras ?: false

        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        setContent {
            MoimTheme {
                IntroNavHost {
                    navigateToMain()
                }
            }
        }
    }
}

private fun Activity.navigateToMain() {
    val intent = Intent(this, Class.forName(MAIN_ACTIVITY_NAME))

    finish()
    startActivity(intent)
}