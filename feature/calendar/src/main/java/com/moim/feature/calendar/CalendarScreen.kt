package com.moim.feature.calendar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_222222

@Composable
fun CalendarRoute(
    viewModel: CalendarViewModel = hiltViewModel(),
    padding: PaddingValues
) {
    Text(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(padding),
        text = "Calendar",
        textAlign = TextAlign.Center,
        style = MoimTheme.typography.heading.bold,
        color = color_222222
    )
}

@Composable
fun CalendarScreen(modifier: Modifier = Modifier) {

}