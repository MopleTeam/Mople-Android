package com.moim.core.designsystem.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.moim.core.designsystem.theme.color_FFFFFF

@Composable
fun EmptyScreen(
    modifier: Modifier = Modifier,
    backgroundColor: Color = color_FFFFFF
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    )
}