package com.moim.feature.alarm.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.NotificationType
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.alarm.AlarmUiAction
import com.moim.feature.alarm.model.AlarmUiModel
import java.time.ZonedDateTime

@Composable
fun AlarmListItem(
    modifier: Modifier = Modifier,
    alarm: AlarmUiModel,
    onUiAction: (AlarmUiAction) -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .onSingleClick { onUiAction(AlarmUiAction.OnClickAlarm(alarm)) }
                .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NetworkImage(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(BorderStroke(1.dp, MoimTheme.colors.stroke), shape = RoundedCornerShape(10.dp))
                    .size(40.dp),
            imageUrl = alarm.meetImageUrl,
            errorImage = painterResource(R.drawable.ic_empty_meeting),
        )
        Spacer(Modifier.size(16.dp))

        Column {
            MoimText(
                text =
                    buildAnnotatedString {
                        withStyle(
                            style =
                                MoimTheme.typography
                                    .title03
                                    .bold
                                    .toSpanStyle(),
                        ) {
                            append(alarm.targetKeyword)
                        }

                        withStyle(
                            style =
                                MoimTheme.typography
                                    .title03
                                    .medium
                                    .toSpanStyle(),
                        ) {
                            append(alarm.title)
                        }
                    },
                color = MoimTheme.colors.text.text01,
                maxLine = 2,
            )
            Spacer(Modifier.size(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MoimText(
                    text = alarm.meetName,
                    style = MoimTheme.typography.body02.medium,
                    color = MoimTheme.colors.text.text03,
                )
                MoimText(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = stringResource(R.string.unit_dot),
                    style = MoimTheme.typography.body02.medium,
                    color = MoimTheme.colors.text.text03,
                )

                MoimText(
                    text = alarm.sendAt.parseDateString(stringResource(R.string.regex_date_year_month_day_short)),
                    style = MoimTheme.typography.body02.medium,
                    color = MoimTheme.colors.text.text03,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AlarmListItemPreview() {
    MoimTheme {
        AlarmListItem(
            alarm =
                AlarmUiModel(
                    notificationId = "0",
                    meetName = "모플",
                    meetImageUrl = "",
                    meetId = "",
                    planId = "",
                    reviewId = "",
                    type = NotificationType.COMMENT_REPLY,
                    title = "의 일정은 어떠셨나요?",
                    targetKeyword = "모플",
                    description = "알람 리스트 메세지 알람 리스트 메세지 알람 리스트 메세지",
                    planDate = ZonedDateTime.now(),
                    sendAt = ZonedDateTime.now(),
                ),
            onUiAction = {},
        )
    }
}
