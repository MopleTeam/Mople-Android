package com.moim.feature.meetingdetail.ui

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.common.util.getDateTimeFormatString
import com.moim.core.common.util.toDecimalString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimCard
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_F6F8FA
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.model.Plan
import com.moim.core.model.Review
import com.moim.feature.meetingdetail.MeetingDetailUiAction
import com.moim.feature.meetingdetail.OnMeetingDetailUiAction
import java.time.ZonedDateTime

@Composable
fun MeetingDetailPlanContent(
    modifier: Modifier = Modifier,
    plans: List<Plan>,
    reviews: List<Review>,
    isPlanSelected: Boolean,
    onUiAction: OnMeetingDetailUiAction
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 28.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoimText(
                    modifier = Modifier.weight(1f),
                    text = stringResource(if (isPlanSelected) R.string.meeting_detail_future_plan else R.string.meeting_detail_past_plan),
                    style = MoimTheme.typography.body01.medium,
                    color = MoimTheme.colors.gray.gray04
                )

                MoimText(
                    text = stringResource(R.string.unit_count, if (isPlanSelected) plans.size else reviews.size),
                    style = MoimTheme.typography.body01.medium,
                    color = MoimTheme.colors.gray.gray04
                )
            }
        }


        if (isPlanSelected) {
            items(
                items = plans,
                key = { it.planId }
            ) {
                MeetingDetailPlanItem(
                    plan = it,
                    onUiAction = onUiAction
                )
            }
        } else {
            items(
                items = reviews,
                key = { it.reviewId }
            ) {
                MeetingDetailReviewItem(
                    review = it,
                    onUiAction = onUiAction
                )
            }
        }
    }
}

@Composable
fun MeetingDetailPlanItem(
    modifier: Modifier = Modifier,
    plan: Plan,
    onUiAction: OnMeetingDetailUiAction
) {
    MoimCard(
        modifier = modifier,
        onClick = { onUiAction(MeetingDetailUiAction.OnClickPlanDetail(plan.planId)) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            MeetingDetailPlanHeader(time = plan.planTime)
            Spacer(Modifier.height(16.dp))

            MoimText(
                modifier = Modifier.fillMaxWidth(),
                text = plan.planName,
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray01
            )
            Spacer(Modifier.height(4.dp))

            PlanParticipantCount(
                count = plan.planMemberCount
            )
            Spacer(Modifier.height(16.dp))

            MeetingWeatherInfo(
                temperature = plan.temperature,
                address = plan.planAddress,
                weatherUrl = plan.weatherIconUrl
            )
            Spacer(Modifier.height(16.dp))

            if (plan.isParticipant) {
                MoimPrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    buttonColors = moimButtomColors().copy(
                        containerColor = MoimTheme.colors.tertiary,
                        contentColor = MoimTheme.colors.gray.gray03
                    ),
                    text = stringResource(R.string.meeting_detail_plan_not_apply),
                    onClick = { onUiAction(MeetingDetailUiAction.OnClickPlanApply(plan.planId, false)) }
                )
            } else {
                MoimPrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    buttonColors = moimButtomColors(),
                    text = stringResource(R.string.meeting_detail_plan_apply),
                    onClick = { onUiAction(MeetingDetailUiAction.OnClickPlanApply(plan.planId, true)) }
                )
            }
        }
    }
}

@Composable
fun MeetingDetailReviewItem(
    modifier: Modifier = Modifier,
    review: Review,
    onUiAction: OnMeetingDetailUiAction
) {
    MoimCard(
        modifier = modifier.padding(16.dp),
        onClick = { onUiAction(MeetingDetailUiAction.OnClickPlanDetail(review.reviewId)) }
    ) {
        Column {
            MeetingDetailPlanHeader(time = review.reviewAt)
            Spacer(Modifier.height(16.dp))

            Row {
                Column {
                    MoimText(
                        modifier = Modifier.fillMaxWidth(),
                        text = review.reviewName,
                        style = MoimTheme.typography.title03.semiBold,
                        color = MoimTheme.colors.gray.gray01
                    )
                    Spacer(Modifier.height(4.dp))

                    PlanParticipantCount(
                        count = review.memberCount
                    )
                }
            }
        }
    }
}

@Composable
private fun MeetingDetailPlanHeader(
    modifier: Modifier = Modifier,
    time: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoimText(
            modifier = modifier.weight(1f),
            text = getDateTimeFormatString(time, stringResource(R.string.regex_date_day_short)),
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
private fun PlanParticipantCount(
    modifier: Modifier = Modifier,
    count: Int,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_menu_meeting),
            contentDescription = "",
            tint = MoimTheme.colors.icon
        )
        Spacer(Modifier.width(4.dp))
        MoimText(
            text = stringResource(R.string.unit_participants_count, count),
            style = MoimTheme.typography.body02.medium,
            color = MoimTheme.colors.gray.gray04
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

@Preview
@Composable
private fun MeetingDetailPlanContentPreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MoimTheme.colors.bg.primary)
                .padding(horizontal = 20.dp),
        ) {
            MeetingDetailPlanContent(
                plans = listOf(
                    Plan(
                        planId = "1",
                        planName = "술 한 잔 하는 날1",
                        planMemberCount = 6,
                        planTime = ZonedDateTime.now().plusDays(2).toString(),
                        planAddress = "서울시 강남구"
                    ),
                    Plan(
                        planId = "2",
                        planName = "술 한 잔 하는 날2",
                        planMemberCount = 6,
                        planTime = ZonedDateTime.now().plusDays(2).toString(),
                        planAddress = "서울시 강남구",
                        isParticipant = true
                    ),
                    Plan(
                        planId = "3",
                        planName = "술 한 잔 하는 날3",
                        planMemberCount = 6,
                        planTime = ZonedDateTime.now().plusDays(2).toString(),
                        planAddress = "서울시 강남구"
                    )
                ),
                reviews = emptyList(),
                isPlanSelected = true,
                onUiAction = {}
            )
        }
    }
}