package com.moim.core.designsystem.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun PagingLoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        CircularProgressIndicator(
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .size(56.dp)
                    .padding(4.dp),
            strokeWidth = 4.dp,
            color = MoimTheme.colors.primary.primary,
        )
    }
}

@Composable
fun PagingErrorScreen(
    modifier: Modifier = Modifier,
    backgroundColor : Color = MoimTheme.colors.white,
    onClickRetry: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MoimText(
            text = stringResource(id = R.string.common_error),
            style = MoimTheme.typography.title02.semiBold,
            color = MoimTheme.colors.gray.gray01,
        )

        Spacer(Modifier.height(8.dp))

        MoimText(
            text = stringResource(id = R.string.common_error_disconnection),
            textAlign = TextAlign.Center,
            style = MoimTheme.typography.body01.regular,
            singleLine = false,
            color = MoimTheme.colors.gray.gray02,
        )

        Spacer(Modifier.height(24.dp))

        MoimPrimaryButton(
            onClick = onClickRetry,
            verticalPadding = 8.dp,
            text = stringResource(R.string.common_refresh)
        )
    }
}

@Preview
@Composable
private fun PagingLoadingScreenPreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.white)
        ) {
            PagingLoadingScreen()
        }
    }
}

@Preview
@Composable
private fun PagingErrorScreenPreview() {
    MoimTheme {
        PagingErrorScreen(
            backgroundColor = MoimTheme.colors.bg.primary
        )
    }
}