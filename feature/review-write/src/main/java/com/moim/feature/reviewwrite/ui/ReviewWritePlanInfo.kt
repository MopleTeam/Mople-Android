package com.moim.feature.reviewwrite.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.Review
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.reviewwrite.ReviewWriteUiAction

@Composable
fun ReviewWritePlanInfo(
    modifier: Modifier = Modifier,
    review: Review,
    onUiAction: (ReviewWriteUiAction) -> Unit = {},
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
                imageUrl = review.meetingImageUrl,
                errorImage = painterResource(R.drawable.ic_empty_meeting),
            )

            Spacer(Modifier.width(8.dp))

            MoimText(
                text = review.meetingName,
                style = MoimTheme.typography.body02.semiBold,
                color = MoimTheme.colors.text.text03,
            )
        }

        Spacer(Modifier.height(12.dp))

        MoimText(
            text = review.reviewName,
            style = MoimTheme.typography.heading.bold,
            color = MoimTheme.colors.text.text01,
            maxLine = 2,
        )

        Spacer(Modifier.height(16.dp))

        PlanInfoItem(
            startIconRes = R.drawable.ic_meeting,
            endIconRes = R.drawable.ic_next,
            enable = true,
            text = stringResource(R.string.unit_participants_count, review.memberCount),
            onClick = { onUiAction(ReviewWriteUiAction.OnClickParticipants) },
        )
        PlanInfoItem(
            modifier = Modifier.padding(vertical = 4.dp),
            startIconRes = R.drawable.ic_clock,
            text = review.reviewAt.parseDateString(stringResource(R.string.regex_date_full)),
        )
        PlanInfoItem(
            startIconRes = R.drawable.ic_location,
            text = review.address.ifEmpty { stringResource(R.string.common_empty_place) },
        )
        Spacer(Modifier.height(80.dp))
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
