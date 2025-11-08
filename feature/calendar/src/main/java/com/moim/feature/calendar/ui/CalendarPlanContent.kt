package com.moim.feature.calendar.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moim.core.common.consts.WEATHER_ICON_URL
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.util.decimalFormatString
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimCard
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_F6F8FA
import com.moim.feature.calendar.CalendarUiAction
import com.moim.feature.calendar.OnCalendarUiAction
import java.time.ZonedDateTime

@Composable
fun CalendarPlanContent(
    modifier: Modifier = Modifier,
    selectDate: ZonedDateTime,
    plans: List<PlanItem> = emptyList(),
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
                MoimText(
                    modifier = Modifier.fillMaxWidth(),
                    text = selectDate.parseDateString(stringResource(R.string.regex_date_year_month_day)),
                    textAlign = TextAlign.Center,
                    style = MoimTheme.typography.body01.medium,
                    color = MoimTheme.colors.gray.gray05,
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        items(
            items = plans,
            key = { plan -> plan.postId }
        ) {
            CalendarPlanItem(
                plan = it,
                onUiAction = onUiAction
            )
        }
    }
}

@Composable
fun CalendarPlanItem(
    modifier: Modifier = Modifier,
    plan: PlanItem,
    onUiAction: OnCalendarUiAction = {}
) {


    MoimCard(
        modifier = modifier,
        onClick = {
            onUiAction(
                CalendarUiAction.OnClickMeetingPlan(
                    if (plan.isPlanAtBefore) {
                        ViewIdType.PlanId(plan.postId)
                    } else {
                        ViewIdType.ReviewId(plan.postId)
                    }
                )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.white)
                .padding(16.dp)
        ) {
            MeetingInfoTopAppbar(
                meetingImageUrl = plan.meetingImageUrl,
                groupName = plan.meetingName
            )
            Spacer(Modifier.height(16.dp))

            MoimText(
                text = plan.planName,
                singleLine = false,
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
                    imageVector = ImageVector.vectorResource(R.drawable.ic_meeting),
                    contentDescription = "",
                    tint = MoimTheme.colors.icon
                )

                MoimText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp),
                    text = stringResource(R.string.unit_participants_count, plan.participantsCount),
                    style = MoimTheme.typography.body02.medium,
                    color = MoimTheme.colors.gray.gray04,
                )
            }
            Spacer(Modifier.height(16.dp))

            MeetingWeatherInfo(
                modifier = Modifier.align(Alignment.Start),
                temperature = plan.temperature,
                address = plan.loadAddress,
                weatherIconUrl = plan.weatherIconUrl,
            )
        }
    }
}

@Composable
private fun MeetingInfoTopAppbar(
    modifier: Modifier = Modifier,
    meetingImageUrl: String,
    groupName: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NetworkImage(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .border(BorderStroke(1.dp, MoimTheme.colors.stroke), RoundedCornerShape(6.dp))
                .size(28.dp),
            imageUrl = meetingImageUrl,
            errorImage = painterResource(R.drawable.ic_empty_meeting)
        )

        MoimText(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = groupName,
            style = MoimTheme.typography.body02.semiBold,
            color = MoimTheme.colors.gray.gray04,
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
    weatherIconUrl: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = color_F6F8FA, shape = RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(32.dp)
                .background(MoimTheme.colors.white)
                .padding(4.dp)
        ) {
            NetworkImage(
                imageUrl = WEATHER_ICON_URL.format(weatherIconUrl),
                errorImage = painterResource(R.drawable.ic_empty_weather)
            )
        }

        if (weatherIconUrl.isEmpty()) {
            MoimText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp),
                text = stringResource(R.string.common_weather_not_found),
                textAlign = TextAlign.Center,
                style = MoimTheme.typography.body02.medium,
                color = MoimTheme.colors.gray.gray04
            )
        } else {
            MoimText(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                text = stringResource(R.string.unit_weather, temperature.decimalFormatString()),
                singleLine = false,
                style = MoimTheme.typography.body01.semiBold,
                color = MoimTheme.colors.gray.gray01
            )

            MoimText(
                text = address,
                singleLine = false,
                style = MoimTheme.typography.body02.medium,
                color = MoimTheme.colors.gray.gray04
            )
        }
    }
}