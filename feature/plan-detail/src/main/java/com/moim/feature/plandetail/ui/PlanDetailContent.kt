package com.moim.feature.plandetail.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction

@Composable
fun PlanDetailContent(
    modifier: Modifier = Modifier,
    isMyPlan: Boolean,
    planItem: PlanItem,
    isShowApplyButton: Boolean,
    onUiAction: OnPlanDetailUiAction = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NetworkImage(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(BorderStroke(1.dp, MoimTheme.colors.stroke), RoundedCornerShape(6.dp))
                        .size(20.dp),
                imageUrl = planItem.meetingImageUrl,
                errorImage = painterResource(R.drawable.ic_empty_meeting),
            )

            Spacer(Modifier.width(8.dp))

            MoimText(
                text = planItem.meetingName,
                style = MoimTheme.typography.body02.semiBold,
                color = MoimTheme.colors.text.text03,
            )
        }

        Spacer(Modifier.height(12.dp))

        MoimText(
            text = planItem.planName,
            style = MoimTheme.typography.heading.bold,
            color = MoimTheme.colors.text.text01,
            maxLine = 2,
        )

        if (planItem.description.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            MoimText(
                text = planItem.description,
                style = MoimTheme.typography.body01.regular,
                color = MoimTheme.colors.text.text02,
                singleLine = false,
            )
        }

        Spacer(Modifier.height(16.dp))

        PlanInfoItem(
            startIconRes = R.drawable.ic_meeting,
            endIconRes = R.drawable.ic_next,
            enable = true,
            text = stringResource(R.string.unit_participants_count, planItem.participantsCount),
            onClick = { onUiAction(PlanDetailUiAction.OnClickParticipants) },
        )

        PlanInfoItem(
            modifier = Modifier.padding(vertical = 4.dp),
            startIconRes = R.drawable.ic_clock,
            text = planItem.planAt.parseDateString(stringResource(R.string.regex_date_full)),
        )
        PlanInfoItem(
            startIconRes = R.drawable.ic_location,
            text = planItem.loadAddress.ifBlank { stringResource(R.string.plan_detail_empty_place) },
        )

        if (planItem.latitude != 0.0 && planItem.longitude != 0.0) {
            Spacer(Modifier.height(16.dp))
            PlanDetailMapContent(
                latitude = planItem.latitude,
                longitude = planItem.longitude,
                onUiAction = onUiAction,
            )
        } else if (isMyPlan && planItem.isPlanAtBefore) {
            Spacer(Modifier.height(20.dp))
            PlanPlaceEmptyCard(
                onClick = { onUiAction(PlanDetailUiAction.OnClickPlanUpdate) },
            )
        }

        if (isShowApplyButton) {
            val btnTextRes = if (planItem.isParticipant) R.string.plan_detail_plan_apply_done else R.string.plan_detail_plan_apply
            val buttonColors =
                moimButtomColors().copy(
                    containerColor = if (planItem.isParticipant) MoimTheme.colors.tertiary else MoimTheme.colors.global.primary,
                    contentColor = if (planItem.isParticipant) MoimTheme.colors.text.text02 else MoimTheme.colors.bg.primary,
                )

            Spacer(Modifier.height(16.dp))
            MoimPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                buttonColors = buttonColors,
                text = stringResource(btnTextRes),
                onClick = {
                    if (planItem.isParticipant) {
                        onUiAction(PlanDetailUiAction.OnShowPlanApplyCancelDialog(true))
                    } else {
                        onUiAction(PlanDetailUiAction.OnClickPlanApply(true))
                    }
                },
            )
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun PlanInfoItem(
    modifier: Modifier = Modifier,
    @DrawableRes startIconRes: Int,
    @DrawableRes endIconRes: Int? = null,
    enable: Boolean = false,
    text: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .onSingleClick(enabled = enable, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(startIconRes),
            contentDescription = "",
            tint = MoimTheme.colors.icon,
        )

        MoimText(
            modifier = Modifier.padding(horizontal = 4.dp),
            text = text,
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.text.text02,
            maxLine = 2,
        )

        if (endIconRes != null) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(endIconRes),
                contentDescription = "",
                tint = MoimTheme.colors.icon,
            )
        }
    }
}

@Composable
private fun PlanPlaceEmptyCard(onClick: () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MoimTheme.colors.bg.input)
                .onSingleClick(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_add),
            contentDescription = "",
            tint = MoimTheme.colors.icon,
        )
        Spacer(Modifier.height(8.dp))
        MoimText(
            text = stringResource(R.string.common_empty_place_for_host),
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray05,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0XFFFFFF)
@Composable
private fun PlanDetailContentPreview() {
    MoimTheme {
        PlanDetailContent(
            isMyPlan = true,
            planItem =
                PlanItem(
                    isPlanAtBefore = true,
                    meetingName = "크리스마스 모험가 클럽",
                    planName = "연말 파티",
                    placeName = "",
                    description = "대신귀\n여운알\n파카를\n드리겠\n습니다",
                ),
            isShowApplyButton = false,
            onUiAction = {},
        )
    }
}
