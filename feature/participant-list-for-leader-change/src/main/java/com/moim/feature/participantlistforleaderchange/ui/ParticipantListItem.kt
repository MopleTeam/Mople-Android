package com.moim.feature.participantlistforleaderchange.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.User
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.participantlistforleaderchange.ParticipantListForLeaderChangeUiAction
import com.moim.feature.participantlistforleaderchange.model.ParticipantListUiModel

@Composable
fun ParticipantListItem(
    modifier: Modifier = Modifier,
    participant: ParticipantListUiModel,
    onUiAction: (ParticipantListForLeaderChangeUiAction) -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .onSingleClick {
                    onUiAction(
                        ParticipantListForLeaderChangeUiAction.OnClickUser(participant.user),
                    )
                }
                .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            NetworkImage(
                modifier =
                    Modifier
                        .clip(shape = CircleShape)
                        .border(BorderStroke(1.dp, MoimTheme.colors.stroke), shape = CircleShape)
                        .size(40.dp)
                        .onSingleClick {
                            onUiAction(ParticipantListForLeaderChangeUiAction.OnClickUserProfile(participant.user))
                        },
                imageUrl = participant.user.profileUrl,
                errorImage = painterResource(R.drawable.ic_empty_user_logo),
            )
        }

        Spacer(Modifier.width(8.dp))

        MoimText(
            modifier = Modifier.weight(1f),
            text = participant.user.nickname,
            style = MoimTheme.typography.title03.medium,
            color = MoimTheme.colors.gray.gray02,
        )

        CheckBox(isSelected = participant.isSelected)
    }
}

@Composable
private fun CheckBox(isSelected: Boolean = false) {
    val borderColor =
        if (isSelected) {
            MoimTheme.colors.global.primary
        } else {
            MoimTheme.colors.blueGray
        }

    Box(
        modifier =
            Modifier
                .size(32.dp)
                .border(BorderStroke(2.dp, borderColor), CircleShape)
                .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                contentDescription = "",
                tint = Color.Unspecified,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ParticipantListItemPreview() {
    MoimTheme {
        Column {
            ParticipantListItem(
                participant =
                    ParticipantListUiModel(
                        user =
                            User(
                                userId = "",
                                nickname = "test",
                            ),
                        isSelected = true,
                    ),
                onUiAction = {},
            )
            ParticipantListItem(
                participant =
                    ParticipantListUiModel(
                        user =
                            User(
                                userId = "",
                                nickname = "test2",
                            ),
                        isSelected = false,
                    ),
                onUiAction = {},
            )
        }
    }
}
