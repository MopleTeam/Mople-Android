package com.moim.feature.meetingdetail.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.common.consts.WEATHER_ICON_URL
import com.moim.core.common.util.parseDateString
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
import com.moim.core.model.ReviewImage
import com.moim.feature.meetingdetail.MeetingDetailUiAction
import com.moim.feature.meetingdetail.OnMeetingDetailUiAction
import java.time.ZonedDateTime

@Composable
fun MeetingDetailPlanContent(
    modifier: Modifier = Modifier,
    userId: String,
    plans: List<Plan>,
    reviews: List<Review>,
    isPlanSelected: Boolean,
    onUiAction: OnMeetingDetailUiAction
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 28.dp, bottom = 64.dp)
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
                    userId = userId,
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
                    userId = userId,
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
    userId: String,
    plan: Plan,
    onUiAction: OnMeetingDetailUiAction
) {
    MoimCard(
        modifier = modifier,
        onClick = { onUiAction(MeetingDetailUiAction.OnClickPlanDetail(plan.planId, true)) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            MeetingDetailPlanHeader(time = plan.planAt)
            Spacer(Modifier.height(16.dp))

            Row {
                if (plan.userId == userId) {
                    MyPostIcon()
                }

                MoimText(
                    modifier = Modifier.fillMaxWidth(),
                    text = plan.planName,
                    style = MoimTheme.typography.title03.semiBold,
                    color = MoimTheme.colors.gray.gray01
                )
            }
            Spacer(Modifier.height(4.dp))

            PlanParticipantCount(
                count = plan.planMemberCount
            )
            Spacer(Modifier.height(16.dp))

            MeetingWeatherInfo(
                temperature = plan.temperature,
                address = plan.weatherAddress,
                weatherIconUrl = plan.weatherIconUrl
            )
            Spacer(Modifier.height(16.dp))

            if (plan.planAt.isBefore(ZonedDateTime.now())) {
                MoimText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    text = stringResource(R.string.meeting_detail_plan_disable),
                    style = MoimTheme.typography.body01.medium,
                    color = MoimTheme.colors.gray.gray05,
                    textAlign = TextAlign.Center,
                )
            } else if (plan.userId != userId) {
                if (plan.isParticipant) {
                    MoimPrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        buttonColors = moimButtomColors().copy(
                            containerColor = MoimTheme.colors.tertiary,
                            contentColor = MoimTheme.colors.gray.gray03
                        ),
                        text = stringResource(R.string.meeting_detail_plan_apply_done),
                        onClick = {
                            onUiAction(
                                MeetingDetailUiAction.OnShowPlanApplyCancelDialog(
                                    isShow = true,
                                    cancelPlanId = plan.planId
                                )
                            )
                        }
                    )
                } else {
                    MoimPrimaryButton(
                        modifier = Modifier.fillMaxWidth(),
                        buttonColors = moimButtomColors(),
                        text = stringResource(R.string.meeting_detail_plan_apply),
                        onClick = {
                            onUiAction(
                                MeetingDetailUiAction.OnClickPlanApply(
                                    isApply = true,
                                    planId = plan.planId,
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MeetingDetailReviewItem(
    modifier: Modifier = Modifier,
    userId: String,
    review: Review,
    onUiAction: OnMeetingDetailUiAction
) {
    MoimCard(
        modifier = modifier,
        onClick = { onUiAction(MeetingDetailUiAction.OnClickPlanDetail(review.reviewId, false)) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            MeetingDetailPlanHeader(time = review.reviewAt)
            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (review.userId == userId) {
                            MyPostIcon()
                        }

                        MoimText(
                            text = review.reviewName,
                            style = MoimTheme.typography.title03.semiBold,
                            color = MoimTheme.colors.gray.gray01
                        )
                    }
                    Spacer(Modifier.height(4.dp))

                    PlanParticipantCount(
                        count = review.memberCount
                    )
                }

                if (review.images.isNotEmpty()) {
                    Spacer(Modifier.width(16.dp))

                    Box {
                        NetworkImage(
                            modifier = Modifier
                                .padding(end = 4.dp, bottom = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .border(BorderStroke(1.dp, MoimTheme.colors.stroke), RoundedCornerShape(6.dp))
                                .size(50.dp),
                            imageUrl = review.images.first().imageUrl,
                            errorImage = painterResource(R.drawable.ic_empty_image)
                        )

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .background(MoimTheme.colors.primary.disable, shape = CircleShape)
                                .border(BorderStroke(1.dp, MoimTheme.colors.stroke), CircleShape)
                                .size(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            MoimText(
                                text = review.images.size.toString(),
                                textAlign = TextAlign.Center,
                                style = MoimTheme.typography.body01.medium,
                                color = MoimTheme.colors.primary.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MyPostIcon() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .padding(2.dp)
            .clip(CircleShape)
            .background(MoimTheme.colors.primary.primary)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(14.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_pen),
            contentDescription = "",
            tint = MoimTheme.colors.white
        )
    }

    Spacer(Modifier.size(4.dp))
}

@Composable
private fun MeetingDetailPlanHeader(
    modifier: Modifier = Modifier,
    time: ZonedDateTime
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoimText(
            modifier = modifier.weight(1f),
            text = time.parseDateString(stringResource(R.string.regex_date_year_month_day_short)),
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
}

@Preview
@Composable
private fun MeetingDetailPlanContentPreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MoimTheme.colors.bg.primary),
        ) {
            MeetingDetailPlanContent(
                userId = "",
                isPlanSelected = false,
                plans = listOf(
                    Plan(
                        userId = "100",
                        planId = "1",
                        planName = "술 한 잔 하는 날1",
                        planMemberCount = 6,
                        planAt = ZonedDateTime.now(),
                        planAddress = "서울시 강남구",
                        isParticipant = true,
                    ),
                    Plan(
                        planId = "2",
                        planName = "술 한 잔 하는 날2",
                        planMemberCount = 6,
                        planAt = ZonedDateTime.now().plusDays(1),
                        planAddress = "서울시 강남구",
                        isParticipant = true
                    ),
                    Plan(
                        planId = "3",
                        planName = "술 한 잔 하는 날3",
                        planMemberCount = 6,
                        planAt = ZonedDateTime.now().plusDays(2),
                        planAddress = "서울시 강남구"
                    )
                ),
                reviews = listOf(
                    Review(
                        postId = "1",
                        meetingId = "1",
                        reviewId = "1",
                        reviewName = "술 한 잔 하는 날1",
                        memberCount = 6,
                        reviewAt = ZonedDateTime.now(),
                        address = "서울시 강남구",
                        images = listOf(ReviewImage(imageId = "", imageUrl = "aaa"))
                    ),
                    Review(
                        postId = "1",
                        meetingId = "1",
                        reviewId = "2",
                        reviewName = "술 한 잔 하는 날2",
                        memberCount = 6,
                        reviewAt = ZonedDateTime.now().plusDays(1),
                        address = "서울시 강남구"
                    ),
                    Review(
                        postId = "1",
                        meetingId = "1",
                        reviewId = "3",
                        reviewName = "술 한 잔 하는 날3",
                        memberCount = 6,
                        reviewAt = ZonedDateTime.now().plusDays(2),
                        address = "서울시 강남구"
                    ),
                    Review(
                        postId = "1",
                        meetingId = "1",
                        reviewId = "4",
                        reviewName = "술 한 잔 하는 날4",
                        memberCount = 6,
                        reviewAt = ZonedDateTime.now().plusDays(3),
                        address = "서울시 강남구"
                    ),
                ),
                onUiAction = {}
            )
        }
    }
}