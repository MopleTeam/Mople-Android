package com.moim.feature.meetingsetting.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.feature.meetingsetting.OnMeetingSettingUiAction
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.feature.meetingsetting.MeetingSettingUiAction

@Composable
fun MeetingSettingTopAppbar(
    modifier: Modifier = Modifier,
    onUiAction: OnMeetingSettingUiAction = {}
) {
    MoimTopAppbar(
        modifier = modifier,
        title = stringResource(R.string.meeting_setting_title),
        onClickNavigate = { onUiAction(MeetingSettingUiAction.OnClickBack) },
    )
}