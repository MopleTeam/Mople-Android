package com.moim.feature.home.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment.Vertical
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.common.util.getDateTimeFormatString
import com.moim.core.common.util.toDecimalString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimCard
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_F6F8FA
import com.moim.core.model.Plan
import com.moim.feature.home.HomeUiAction
import com.moim.feature.home.OnHomeUiAction
import java.time.ZonedDateTime

@Composable
fun HomeMeetingPlanCard(
    modifier: Modifier = Modifier,
    plan: Plan,
    onUiAction: OnHomeUiAction = {}
) {
    MoimCard(
        modifier = modifier,
        onClick = { onUiAction(HomeUiAction.OnClickMeeting(meetingId = plan.meetingId)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            MeetingInfoTopAppbar(
                groupName = plan.meetingName
            )
            Spacer(Modifier.height(16.dp))

            MoimText(
                text = plan.planName,
                style = MoimTheme.typography.title01.bold,
                color = MoimTheme.colors.gray.gray01
            )
            Spacer(Modifier.height(16.dp))

            MeetingInfoText(
                iconRes = R.drawable.ic_meeting,
                text = stringResource(R.string.unit_participants_count, plan.planMemberCount)
            )
            MeetingInfoText(
                modifier = Modifier.padding(vertical = 4.dp),
                iconRes = R.drawable.ic_clock,
                text = getDateTimeFormatString(dateTime = plan.planTime, pattern = stringResource(R.string.regex_date_full))
            )
            MeetingInfoText(
                iconRes = R.drawable.ic_location,
                text = plan.planAddress,
            )
            Spacer(Modifier.height(16.dp))

            MeetingWeatherInfo(
                modifier = Modifier.align(Alignment.Start),
                temperature = plan.temperature,
                address = plan.planAddress,
                weatherUrl = plan.weatherIconUrl
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
private fun MeetingInfoText(
    modifier: Modifier = Modifier,
    verticalGravity: Vertical = Alignment.CenterVertically,
    maxLines: Int = 1,
    @DrawableRes iconRes: Int,
    text: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = verticalGravity
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = "",
            tint = MoimTheme.colors.icon
        )

        MoimText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp),
            text = text,
            style = MoimTheme.typography.body02.medium,
            color = MoimTheme.colors.gray.gray04,
            maxLine = maxLines,
            minLine = maxLines,
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

        MoimText(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            text = stringResource(R.string.unit_weather, temperature.toDecimalString()),
            style = MoimTheme.typography.body01.semiBold,
            color = MoimTheme.colors.gray.gray01
        )

        MoimText(
            text = address,
            style = MoimTheme.typography.body02.medium,
            color = MoimTheme.colors.gray.gray04
        )
    }
}

@Preview
@Composable
private fun HomeMeetingPlanCardPreview() {
    MoimTheme {
        HomeMeetingPlanCard(
            plan = Plan(
                meetingId = "1",
                meetingName = "우리중학교 동창1",
                planName = "술 한잔 하는 날",
                planMemberCount = 3,
                planAddress = "서울 강남구",
                planTime = ZonedDateTime.now().toString()
            ),
        )
    }
}