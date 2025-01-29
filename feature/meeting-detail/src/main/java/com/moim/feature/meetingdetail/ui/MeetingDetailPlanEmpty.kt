package com.moim.feature.meetingdetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun MeetingDetailPlanEmpty(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_empty_calendar),
            contentDescription = "",
            tint = MoimTheme.colors.icon
        )
        MoimText(
            text = stringResource(R.string.meeting_detail_plan_empty),
            singleLine = false,
            style = MoimTheme.typography.title03.medium,
            color = MoimTheme.colors.gray.gray06
        )
        Spacer(Modifier.height(80.dp))
    }
}

@Preview
@Composable
fun MeetingDetailPlanEmptyPreview(
    modifier: Modifier = Modifier
) {
    MoimTheme {
        MeetingDetailPlanEmpty(
            modifier = Modifier.background(MoimTheme.colors.bg.primary)
        )
    }
}