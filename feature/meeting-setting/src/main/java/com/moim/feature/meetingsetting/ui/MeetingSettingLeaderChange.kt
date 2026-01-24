package com.moim.feature.meetingsetting.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.ViewIdType
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meetingsetting.MeetingSettingUiAction
import com.moim.feature.meetingsetting.OnMeetingSettingUiAction

@Composable
fun MeetingSettingLeaderChange(
    modifier: Modifier = Modifier,
    meetingId: String,
    onUiAction: OnMeetingSettingUiAction,
) {
    val meetIdType = ViewIdType.MeetId(meetingId)

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .onSingleClick {
                    onUiAction(MeetingSettingUiAction.OnClickMeetingLeaderChange(meetIdType))
                }
                .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoimText(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.meeting_setting_participants_leader_change),
            style = MoimTheme.typography.title03.medium,
            color = MoimTheme.colors.text.text01,
        )
    }
}
