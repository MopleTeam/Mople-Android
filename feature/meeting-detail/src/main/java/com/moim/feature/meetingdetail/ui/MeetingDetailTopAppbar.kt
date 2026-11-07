package com.moim.feature.meetingdetail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.Meeting
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meetingdetail.model.MeetingDetailIntent

@Composable
fun MeetingDetailTopAppbar(
    meeting: Meeting,
    onIntent: (MeetingDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    MoimTopAppbar(
        modifier = modifier,
        actions = {
            MoimIconButton(
                iconRes = R.drawable.ic_notice,
                onClick = { onIntent(MeetingDetailIntent.MeetingNoticeClick) },
            )
            Spacer(Modifier.width(8.dp))
            MoimIconButton(
                iconRes = R.drawable.ic_burger,
                onClick = { onIntent(MeetingDetailIntent.MeetingSettingClick) },
            )
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NetworkImage(
                    modifier =
                        Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(BorderStroke(1.dp, MoimTheme.colors.stroke), RoundedCornerShape(6.dp))
                            .onSingleClick {
                                onIntent(
                                    MeetingDetailIntent.MeetingImageClick(
                                        imageUrl = meeting.imageUrl,
                                        meetingName = meeting.name,
                                    ),
                                )
                            },
                    imageUrl = meeting.imageUrl,
                    errorImage = painterResource(R.drawable.ic_empty_meeting),
                )

                Spacer(Modifier.width(8.dp))

                MoimText(
                    text = meeting.name,
                    style = MoimTheme.typography.body01.medium,
                    color = MoimTheme.colors.text.text01,
                )
            }
        },
        onClickNavigate = { onIntent(MeetingDetailIntent.BackClick) },
    )
}

@ThemePreviews
@Composable
private fun MeetingDetailTopAppbarPreview() {
    MoimTheme {
        MeetingDetailTopAppbar(
            meeting =
                Meeting(
                    id = "0",
                    name = "우리중학교 동창",
                ),
            onIntent = {},
        )
    }
}
