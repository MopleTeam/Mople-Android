package com.moim.feature.reviewwrite.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.moim.core.common.util.decimalFormatString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.common.model.ReviewImage
import com.moim.feature.reviewwrite.ReviewWriteUiAction

@Composable
fun ReviewWriteUploadImageContainer(
    modifier: Modifier = Modifier,
    images: List<ReviewImage>,
    onUiAction: (ReviewWriteUiAction) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MoimText(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.review_write_photo_title),
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray01,
            )

            MoimText(
                text = stringResource(R.string.unit_count, images.size.decimalFormatString()),
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray04,
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            if (images.size <= 4) {
                item {
                    ImageUploadBox(
                        modifier = Modifier.animateItem(),
                        onUiAction = onUiAction
                    )
                }
            }

            items(
                items = images,
            ) {
                ReviewImageBox(
                    modifier = Modifier.animateItem(),
                    image = it,
                    onUiAction = onUiAction
                )
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun ImageUploadBox(
    modifier: Modifier = Modifier,
    onUiAction: (ReviewWriteUiAction) -> Unit
) {
    Box(
        modifier = modifier
            .size(110.dp)
            .clip(RoundedCornerShape(8.dp))
            .onSingleClick { onUiAction(ReviewWriteUiAction.OnClickImageUpload) }
            .border(BorderStroke(1.dp, MoimTheme.colors.stroke))
            .background(MoimTheme.colors.bg.primary)
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp),
            imageVector = Icons.Default.Add,
            contentDescription = "",
            tint = MoimTheme.colors.icon,
        )
    }
}

@Composable
private fun ReviewImageBox(
    modifier: Modifier = Modifier,
    image: ReviewImage,
    onUiAction: (ReviewWriteUiAction) -> Unit
) {
    Box(
        modifier = modifier,
    ) {
        NetworkImage(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(8.dp)),
            imageUrl = image.imageUrl,
            errorImage = painterResource(R.drawable.ic_empty_image)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .clip(CircleShape)
                .border(BorderStroke(1.dp, MoimTheme.colors.stroke), CircleShape)
                .background(MoimTheme.colors.primary.primary)
        ) {
            Icon(
                modifier = Modifier
                    .padding(4.dp)
                    .onSingleClick { onUiAction(ReviewWriteUiAction.OnClickRemoveImage(image)) },
                imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                contentDescription = "",
                tint = MoimTheme.colors.white
            )
        }
    }
}

@Preview
@Composable
private fun ReviewWriteUploadImageContainerPreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.white)
        ) {
            ReviewWriteUploadImageContainer(
                images = emptyList(),
            )
        }
    }
}