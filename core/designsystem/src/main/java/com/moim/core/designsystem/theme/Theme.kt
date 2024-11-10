package com.moim.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val LightColorScheme = lightColorScheme(
    background = color_FFFFFF,
    surfaceTint = color_000000
)

@Composable
fun MoimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    CompositionLocalProvider(
        LocalDensity provides Density(density = LocalDensity.current.density, fontScale = 1f),
        LocalTypography provides Typography,
        LocalMoimColors provides MoimColor()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

object MoimTheme {
    val typography: MoimTypography
        @Composable
        get() = LocalTypography.current

    val colors: MoimColor
        @Composable
        get() = LocalMoimColors.current
}