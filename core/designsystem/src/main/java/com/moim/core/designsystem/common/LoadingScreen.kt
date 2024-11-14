package com.moim.core.designsystem.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(64.dp)
                .padding(8.dp),
            strokeWidth = 4.dp,
            color = MoimTheme.colors.primary.primary
        )
    }
}

@Composable
fun LoadingDialog(isShow: Boolean) {
    AnimatedVisibility(visible = isShow) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
        ) {
            Box(modifier = Modifier) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(64.dp)
                        .padding(8.dp),
                    strokeWidth = 4.dp,
                    color = MoimTheme.colors.primary.primary
                )
            }
        }
    }
}