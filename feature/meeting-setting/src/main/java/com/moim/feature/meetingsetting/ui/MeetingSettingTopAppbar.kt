package com.moim.feature.meetingsetting.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.feature.meetingsetting.OnMeetingSettingIntent
import com.moim.feature.meetingsetting.model.MeetingSettingIntent

@Composable
fun MeetingSettingTopAppbar(
    modifier: Modifier = Modifier,
    onIntent: OnMeetingSettingIntent = {},
) {
    MoimTopAppbar(
        modifier = modifier,
        title = stringResource(R.string.meeting_setting_title),
        onClickNavigate = { onIntent(MeetingSettingIntent.BackClick) },
    )
}
