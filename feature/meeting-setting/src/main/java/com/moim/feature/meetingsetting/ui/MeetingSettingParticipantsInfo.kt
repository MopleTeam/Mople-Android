package com.moim.feature.meetingsetting.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Meeting
import com.moim.feature.meetingsetting.MeetingSettingUiAction
import com.moim.feature.meetingsetting.OnMeetingSettingUiAction


@Composable
fun MeetingSettingParticipantsInfo(
    meeting: Meeting,
    onUiAction: OnMeetingSettingUiAction
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSingleClick { onUiAction(MeetingSettingUiAction.OnClickMeetingParticipants(meeting.id)) }
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoimText(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.meeting_setting_participants),
            style = MoimTheme.typography.title03.medium,
            color = MoimTheme.colors.gray.gray01
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            MoimText(
                text = stringResource(R.string.unit_participants_count_short, meeting.memberCount),
                style = MoimTheme.typography.title03.medium,
                color = MoimTheme.colors.gray.gray04
            )
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_next),
                contentDescription = "",
                tint = MoimTheme.colors.icon
            )
        }
    }
}