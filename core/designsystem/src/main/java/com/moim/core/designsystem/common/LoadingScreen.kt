package com.moim.core.designsystem.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(46.dp)
                    .clip(CircleShape)
                    .padding(4.dp)
                    .border(BorderStroke(4.dp, MoimTheme.colors.stroke), CircleShape),
        )

        CircularProgressIndicator(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(46.dp)
                    .padding(4.dp),
            strokeWidth = 4.dp,
            color = MoimTheme.colors.gray.gray05,
        )
    }
}

@Composable
fun LoadingDialog(isShow: Boolean) {
    AnimatedVisibility(visible = isShow) {
        Dialog(
            onDismissRequest = {},
            properties =
                DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                ),
        ) {
            Box {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .size(46.dp)
                            .clip(CircleShape)
                            .padding(4.dp)
                            .border(BorderStroke(4.dp, MoimTheme.colors.stroke), CircleShape),
                )

                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .size(46.dp)
                            .padding(4.dp),
                    strokeWidth = 4.dp,
                    color = MoimTheme.colors.gray.gray05,
                )
            }
        }
    }
}
