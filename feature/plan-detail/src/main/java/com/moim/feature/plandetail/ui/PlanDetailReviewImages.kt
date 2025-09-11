package com.moim.feature.plandetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.ReviewImage
import com.moim.core.common.util.decimalFormatString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction

@Composable
fun PlanDetailReviewImages(
    modifier: Modifier = Modifier,
    images: List<ReviewImage>,
    onUiAction: OnPlanDetailUiAction
) {
    if (images.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
                .padding(horizontal = 20.dp)
        ) {
            MoimText(
                text = stringResource(R.string.plan_detail_image),
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray01
            )

            Spacer(Modifier.weight(1f))

            MoimText(
                text = stringResource(R.string.unit_count, images.size.decimalFormatString()),
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray04
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(20.dp),
        ) {
            items(
                count = images.size,
            ) { index ->
                NetworkImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(109.dp)
                        .onSingleClick { onUiAction(PlanDetailUiAction.OnClickReviewImage(index)) },
                    errorImage = painterResource(R.drawable.ic_empty_image),
                    imageUrl = images[index].imageUrl
                )
            }
        }
    }
}