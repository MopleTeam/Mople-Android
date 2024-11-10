package com.moim.feature.home.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimCard
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.home.HomeUiAction
import com.moim.feature.home.OnHomeUiAction

@Composable
fun HomeCreateCards(
    modifier: Modifier = Modifier,
    onUiAction: OnHomeUiAction = {},
) {
    Row(
        modifier = modifier.padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        HomeCreateCard(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_new_group,
            text = stringResource(R.string.home_new_group_created),
            onClick = { onUiAction(HomeUiAction.OnClickMeetingWrite) }
        )

        HomeCreateCard(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_new_calendar,
            text = stringResource(R.string.home_new_meeting_created),
            onClick = { onUiAction(HomeUiAction.OnClickPlanWrite) }
        )
    }
}

@Composable
fun HomeCreateCard(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    text: String,
    onClick: () -> Unit = {}
) {
    MoimCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = text,
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray01
            )
            Spacer(Modifier.height(4.dp))

            Icon(
                modifier = Modifier
                    .align(Alignment.End)
                    .size(40.dp),
                imageVector = ImageVector.vectorResource(iconRes),
                contentDescription = "",
                tint = Color.Unspecified
            )
        }
    }
}

@Preview
@Composable
private fun HomeCreateCardPreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.bg.primary),
        ) {
            HomeCreateCards()
        }
    }
}