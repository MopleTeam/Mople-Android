package com.moim.feature.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.moim.core.common.util.getDateTimeFormatZoneDate
import com.moim.core.common.util.toDecimalString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimCard
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_F6F8FA
import com.moim.core.model.MeetingPlan
import com.moim.feature.calendar.CalendarUiAction
import com.moim.feature.calendar.OnCalendarUiAction
import java.time.ZonedDateTime

@Composable
fun CalendarPlanContent(
    modifier: Modifier = Modifier,
    selectDate: ZonedDateTime,
    meetingPlans: List<MeetingPlan> = emptyList(),
    onUiAction: OnCalendarUiAction = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MoimTheme.colors.bg.primary),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 28.dp, horizontal = 20.dp)
    ) {
        item {
            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = getDateTimeFormatZoneDate(selectDate, stringResource(R.string.regex_date_day)),
                    textAlign = TextAlign.Center,
                    style = MoimTheme.typography.body01.medium,
                    color = MoimTheme.colors.gray.gray05,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        items(
            items = meetingPlans,
            key = { plan -> plan.id }
        ) {
            CalendarPlanItem(
                meetingPlan = it,
                onUiAction = onUiAction
            )
        }
    }
}

@Composable
fun CalendarPlanItem(
    modifier: Modifier = Modifier,
    meetingPlan: MeetingPlan,
    onUiAction: OnCalendarUiAction = {}
) {
    MoimCard(
        modifier = modifier,
        onClick = { onUiAction(CalendarUiAction.OnClickMeetingPlan(id = meetingPlan.meetingId)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.white)
                .padding(16.dp)
        ) {
            MeetingInfoTopAppbar(
                groupName = meetingPlan.meetingName
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = meetingPlan.name,
                style = MoimTheme.typography.title02.bold,
                color = MoimTheme.colors.gray.gray01
            )
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_group),
                    contentDescription = "",
                    tint = MoimTheme.colors.icon
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp),
                    text = stringResource(R.string.unit_participants_count, meetingPlan.participants.size),
                    style = MoimTheme.typography.body02.medium,
                    color = MoimTheme.colors.gray.gray04,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(16.dp))

            MeetingWeatherInfo(
                modifier = Modifier.align(Alignment.Start),
                temperature = meetingPlan.temperature,
                address = meetingPlan.address,
                weatherUrl = meetingPlan.weatherIconUrl
            )
        }
    }
}

@Composable
private fun MeetingInfoTopAppbar(
    modifier: Modifier = Modifier,
    groupName: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NetworkImage(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp)),
            imageUrl = "https://plus.unsplash.com/premium_photo-1670333183316-ab697ddd9b13",
            errorImage = painterResource(R.drawable.ic_meeting_empty)
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = groupName,
            style = MoimTheme.typography.body02.semiBold,
            color = MoimTheme.colors.gray.gray04,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_next),
            contentDescription = "",
            tint = MoimTheme.colors.icon
        )
    }
}

@Composable
private fun MeetingWeatherInfo(
    modifier: Modifier = Modifier,
    temperature: Float,
    address: String,
    weatherUrl: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = color_F6F8FA, shape = RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NetworkImage(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp)),
            imageUrl = weatherUrl,
        )

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            text = stringResource(R.string.unit_weather, temperature.toDecimalString()),
            style = MoimTheme.typography.body01.semiBold,
            color = MoimTheme.colors.gray.gray01
        )

        Text(
            text = address,
            style = MoimTheme.typography.body02.medium,
            color = MoimTheme.colors.gray.gray04
        )
    }
}