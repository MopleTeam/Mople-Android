package com.moim.feature.meeting.ui

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimCard
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_222222
import com.moim.core.designsystem.theme.color_888888
import com.moim.core.designsystem.theme.color_F5F5F5
import com.moim.core.designsystem.theme.color_F6F8FA
import com.moim.core.model.Meeting
import com.moim.feature.meeting.MeetingUiAction
import com.moim.feature.meeting.OnMeetingUiAction

@Composable
fun MeetingCard(
    modifier: Modifier = Modifier,
    meeting: Meeting,
    onUiAction: OnMeetingUiAction = {}
) {
    MoimCard(
        modifier = modifier,
        onClick = { onUiAction(MeetingUiAction.OnClickMeeting(meetingId = meeting.id)) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                NetworkImage(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    imageUrl = meeting.imageUrl
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = meeting.name,
                        style = MoimTheme.typography.title03.semiBold,
                        color = color_222222,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_group),
                            contentDescription = "",
                            tint = Color.Unspecified
                        )

                        Text(
                            text = stringResource(R.string.unit_participants_count_short, meeting.members.size),
                            style = MoimTheme.typography.body02.medium,
                            color = color_888888
                        )
                    }
                }

                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_prev),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = color_F6F8FA, shape = RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = stringResource(R.string.meeting_new_meeting),
                    style = MoimTheme.typography.body01.medium,
                    color = color_888888,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
private fun MeetingCardPreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = color_F5F5F5)
                .padding(12.dp)
        ) {
            MeetingCard(
                meeting = Meeting(name = "우리중학교 동창")
            )
        }
    }
}