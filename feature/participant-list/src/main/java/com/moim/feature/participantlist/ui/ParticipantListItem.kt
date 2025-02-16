package com.moim.feature.participantlist.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Participant

@Composable
fun ParticipantListItem(
    modifier: Modifier = Modifier,
    isMeeting: Boolean,
    participant: Participant,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {

            NetworkImage(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .border(BorderStroke(1.dp, MoimTheme.colors.stroke), shape = CircleShape)
                    .size(40.dp),
                imageUrl = participant.imageUrl,
                errorImage = painterResource(R.drawable.ic_empty_image),
            )

            if (participant.isCreator) {
                CreatorIcon(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    isMeeting = isMeeting
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        MoimText(
            text = participant.nickname,
            style = MoimTheme.typography.title03.medium,
            color = MoimTheme.colors.gray.gray02
        )
    }
}

@Composable
private fun CreatorIcon(
    modifier: Modifier = Modifier,
    isMeeting: Boolean
) {
    val creatorIcon = if (isMeeting) R.drawable.ic_crown else R.drawable.ic_editor

    Icon(
        modifier = modifier.size(16.dp),
        imageVector = ImageVector.vectorResource(creatorIcon),
        contentDescription = "",
        tint = Color.Unspecified
    )
}

@Preview
@Composable
private fun ParticipantListItemPreview() {
    MoimTheme {
        ParticipantListItem(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.white),
            isMeeting = true,
            participant = Participant(
                isCreator = true,
                memberId = "",
                imageUrl = "",
                nickname = "퉁퉁이",
            ),
        )
    }
}