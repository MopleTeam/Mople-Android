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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.util.getDateTimeFormatString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Notification
import com.moim.feature.alarm.AlarmUiAction

@Composable
fun AlarmListItem(
    modifier: Modifier = Modifier,
    notification: Notification,
    onUiAction: (AlarmUiAction) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .onSingleClick { onUiAction(AlarmUiAction.OnClickAlarm(notification)) }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NetworkImage(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(BorderStroke(1.dp, MoimTheme.colors.stroke), shape = RoundedCornerShape(10.dp))
                .size(40.dp),
            imageUrl = notification.meetImgUrl
        )
        Spacer(Modifier.size(16.dp))

        Column {
            MoimText(
                text = notification.payload.title,
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray02,
                maxLine = 2
            )
            Spacer(Modifier.size(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoimText(
                    text = notification.meetName,
                    style = MoimTheme.typography.body02.medium,
                    color = MoimTheme.colors.gray.gray04,
                )
                MoimText(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = stringResource(R.string.unit_dot),
                    style = MoimTheme.typography.body02.medium,
                    color = MoimTheme.colors.gray.gray04,
                )

                MoimText(
                    text = getDateTimeFormatString(
                        dateTime = notification.sendAt,
                        pattern = stringResource(R.string.regex_date_year_month_day_short)
                    ),
                    style = MoimTheme.typography.body02.medium,
                    color = MoimTheme.colors.gray.gray04,
                )
            }
        }
    }
}