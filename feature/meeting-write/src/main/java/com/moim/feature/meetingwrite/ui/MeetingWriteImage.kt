package com.moim.feature.meetingwrite.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meetingwrite.MeetingWriteUiAction
import com.moim.feature.meetingwrite.OnMeetingWriteUiAction

@Composable
fun MeetingWriteImage(
    modifier: Modifier = Modifier,
    meetingImageUrl: String?,
    onUiAction: OnMeetingWriteUiAction
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            if (meetingImageUrl.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(color = MoimTheme.colors.bg.primary)
                        .align(Alignment.Center)
                        .onSingleClick { onUiAction(MeetingWriteUiAction.OnShowMeetingPhotoEditDialog(true)) }
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_empty_meeting),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                }
            } else {
                NetworkImage(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .align(Alignment.Center)
                        .onSingleClick { onUiAction(MeetingWriteUiAction.OnShowMeetingPhotoEditDialog(true)) },
                    imageUrl = meetingImageUrl,
                    errorImage = painterResource(R.drawable.ic_empty_image),
                )
            }

            Icon(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = 65.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}

@Preview
@Composable
private fun MeetingWriteImagePreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.white)
        ) {
            MeetingWriteImage(
                meetingImageUrl = null,
                onUiAction = {}
            )
        }
    }
}