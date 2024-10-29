package com.moim.feature.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.intro.navigation.IntroNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val mainActivityName = "com.moim.feature.main.MainActivity"
    val intent = Intent(this, Class.forName(mainActivityName))

    finish()
    startActivity(intent)
}