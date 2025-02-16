package com.moim.feature.meetingsetting.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Meeting
import com.moim.feature.meetingsetting.MeetingSettingUiAction
import com.moim.feature.meetingsetting.OnMeetingSettingUiAction

@Composable
fun MeetingSettingProfile(
    modifier: Modifier = Modifier,
    meeting: Meeting,
    isMeetingHost: Boolean,
    onUiAction: OnMeetingSettingUiAction = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .onSingleClick {
                    if (isMeetingHost) {
                        onUiAction(MeetingSettingUiAction.OnClickMeetingEdit(meeting))
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            NetworkImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(BorderStroke(1.dp, MoimTheme.colors.stroke), shape = RoundedCornerShape(10.dp))
                    .size(40.dp),
                imageUrl = meeting.imageUrl,
                errorImage = painterResource(R.drawable.ic_empty_meeting)
            )

            Spacer(Modifier.width(12.dp))

            MoimText(
                text = meeting.name,
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray01
            )
            if (isMeetingHost) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_pen),
                    contentDescription = "",
                    tint = MoimTheme.colors.icon
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.bg.input, shape = RoundedCornerShape(8.dp)),
            horizontalArrangement = Arrangement.Center
        ) {
            MoimText(
                modifier = Modifier.padding(12.dp),
                text = buildAnnotatedString {
                    append(stringResource(R.string.meeting_setting_date_01))
                    withStyle(style = SpanStyle(color = MoimTheme.colors.primary.primary)) { append(" ${meeting.meetStartDate} ") }
                    append(stringResource(R.string.meeting_setting_date_02))
                },
                overflow = TextOverflow.Ellipsis,
                style = MoimTheme.typography.body01.medium,
                color = MoimTheme.colors.gray.gray04,
            )
        }
    }
}

@Preview
@Composable
private fun MeetingSettingProfilePreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.white)
        ) {
            MeetingSettingProfile(
                meeting = Meeting(
                    name = "우리중학교 동창",
                    meetStartDate = 12,
                ),
                isMeetingHost = true
            )
        }
    }
}