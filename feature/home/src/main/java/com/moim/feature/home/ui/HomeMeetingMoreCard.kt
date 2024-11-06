package com.moim.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimCard
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_555555
import com.moim.feature.home.HomeUiAction
import com.moim.feature.home.OnHomeUiAction

@Composable
fun HomeMeetingMoreCard(
    modifier: Modifier = Modifier,
    onUiAction: OnHomeUiAction
) {
    MoimCard(
        modifier = modifier.fillMaxHeight(),
        onClick = { onUiAction(HomeUiAction.OnClickMeetingMore) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "",
                tint = color_555555
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.home_meeting_more),
                style = MoimTheme.typography.title03.bold,
                color = color_555555
            )
        }
    }
}

@Preview
@Composable
private fun HomeMeetingMoreCardPreview() {
    MoimTheme {
        HomeMeetingMoreCard(
            modifier = Modifier.height(240.dp),
            onUiAction = {}
        )
    }
}