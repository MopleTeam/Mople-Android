package com.moim.feature.meeting.ui

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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.Meeting
import com.moim.core.common.util.getDateTimeBetweenDay
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimCard
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meeting.MeetingUiAction
import kotlin.math.absoluteValue

@Composable
fun MeetingCard(
    modifier: Modifier = Modifier,
    meeting: Meeting,
    onUiAction: (MeetingUiAction) -> Unit = {},
) {
    val (count, comment) =
        meeting.lastPlanAt
            ?.let { lastDate ->
                val day = getDateTimeBetweenDay(endDate = lastDate)
                when {
                    day == 0 -> stringResource(R.string.meeting_plan_today_count) to stringResource(R.string.meeting_plan_comment)
                    day > 0 -> stringResource(R.string.meeting_plan_after_count, day) to stringResource(R.string.meeting_plan_comment)
                    else -> null to stringResource(R.string.meeting_plan_before, day.absoluteValue)
                }
            } ?: run { null to stringResource(R.string.meeting_plan_new) }

    MoimCard(
        modifier = modifier,
        onClick = { onUiAction(MeetingUiAction.OnClickMeeting(meetingId = meeting.id)) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NetworkImage(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, MoimTheme.colors.stroke), shape = RoundedCornerShape(12.dp))
                            .size(56.dp),
                    imageUrl = meeting.imageUrl,
                    errorImage = painterResource(R.drawable.ic_empty_meeting),
                )

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    MoimText(
                        text = meeting.name,
                        style = MoimTheme.typography.title03.semiBold,
                        color = MoimTheme.colors.gray.gray01,
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_meeting),
                            contentDescription = "",
                            tint = MoimTheme.colors.icon,
                        )

                        MoimText(
                            text = stringResource(R.string.unit_participants_count_short, meeting.memberCount),
                            style = MoimTheme.typography.body02.medium,
                            color = MoimTheme.colors.gray.gray04,
                        )
                    }
                }

                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_next),
                    contentDescription = "",
                    tint = MoimTheme.colors.icon,
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(color = MoimTheme.colors.bg.input, shape = RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                MoimText(
                    modifier = Modifier.padding(12.dp),
                    text =
                        buildAnnotatedString {
                            if (count != null) {
                                withStyle(style = SpanStyle(color = MoimTheme.colors.primary.primary)) { append(count.plus(" ")) }
                            }
                            append(comment)
                        },
                    style = MoimTheme.typography.body01.medium,
                    color = MoimTheme.colors.gray.gray04,
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
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(color = MoimTheme.colors.bg.primary)
                    .padding(12.dp),
        ) {
            MeetingCard(
                meeting = Meeting(name = "우리중학교 동창"),
            )
        }
    }
}
