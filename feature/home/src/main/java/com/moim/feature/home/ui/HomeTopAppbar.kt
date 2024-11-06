package com.moim.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_3366FF
import com.moim.core.designsystem.theme.color_F5F5F5
import com.moim.feature.home.HomeUiAction
import com.moim.feature.home.OnHomeUiAction

@Composable
fun HomeTopAppbar(
    modifier: Modifier = Modifier,
    onUiAction: OnHomeUiAction = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color_F5F5F5)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MoimTheme.typography.title01.bold,
            color = color_3366FF
        )

        Spacer(Modifier.weight(1f))

        MoimIconButton(
            onClick = { onUiAction(HomeUiAction.OnClickAlarm) },
            iconRes = R.drawable.ic_alarm,
        )
    }
}

@Preview
@Composable
private fun HomeTopAppbarPreview() {
    MoimTheme {
        HomeTopAppbar()
    }
}