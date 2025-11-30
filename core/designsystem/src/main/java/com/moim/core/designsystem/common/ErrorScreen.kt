package com.moim.core.designsystem.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    onClickRefresh: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_warning_fill),
            contentDescription = "",
            tint = Color.Unspecified
        )

        Spacer(Modifier.height(24.dp))

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
            onClick = onClickRefresh,
            verticalPadding = 8.dp,
            text = stringResource(R.string.common_refresh)
        )
    }
}

@Composable
fun NotFoundErrorScreen(
    modifier: Modifier = Modifier,
    description: String? = null,
    onClickBack: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_warning),
            contentDescription = "",
            tint = Color.Unspecified
        )

        Spacer(Modifier.height(8.dp))


        MoimText(
            text = description ?: stringResource(id = R.string.common_error_not_found_feed),
            textAlign = TextAlign.Center,
            style = MoimTheme.typography.title03.medium,
            singleLine = false,
            color = MoimTheme.colors.gray.gray05,
        )

        Spacer(Modifier.weight(1f))

        MoimPrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            onClick = onClickBack,
            text = stringResource(R.string.common_error_not_found_feed_btn)
        )

        Spacer(Modifier.height(8.dp))
    }
}


@Preview
@Composable
private fun ErrorScreenPreview() {
    MoimTheme {
        ErrorScreen(
            modifier = Modifier.containerScreen(),
            onClickRefresh = {}
        )
    }
}

@Preview
@Composable
private fun NotFoundErrorScreenPreview() {
    MoimTheme {
        NotFoundErrorScreen(
            modifier = Modifier.containerScreen(),
            onClickBack = {}
        )
    }
}