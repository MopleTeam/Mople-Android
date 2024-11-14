package com.moim.feature.main

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moim.core.common.consts.INTRO_ACTIVITY_NAME
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.main.screen.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT))
        setContent {
            MoimTheme {
                MainScreen {
                    navigateToIntro()
                }
            }
        }
    }
}

private fun Activity.navigateToIntro() {
    val intent = Intent(this, Class.forName(INTRO_ACTIVITY_NAME))
    finish()
    startActivity(intent)
}