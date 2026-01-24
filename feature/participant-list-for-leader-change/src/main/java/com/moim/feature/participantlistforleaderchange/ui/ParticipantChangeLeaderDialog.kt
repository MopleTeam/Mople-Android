package com.moim.feature.participantlistforleaderchange.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.User
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.participantlistforleaderchange.ParticipantListForLeaderChangeUiAction

@Composable
fun ParticipantChangeLeaderDialog(
    user: User,
    onUiAction: (ParticipantListForLeaderChangeUiAction) -> Unit,
) {
    val dismissAction = ParticipantListForLeaderChangeUiAction.ShowChangeLeaderDialog(false)

    MoimAlertDialog(
        title = stringResource(R.string.participant_list_for_leader_change_request),
        positiveText = stringResource(R.string.common_positive),
        negativeText = stringResource(R.string.common_negative),
        onClickPositive = {
            onUiAction(dismissAction)
            onUiAction(ParticipantListForLeaderChangeUiAction.OnClickLeaderChange(user.userId))
        },
        onClickNegative = { onUiAction(dismissAction) },
        onDismiss = { onUiAction(dismissAction) },
    ) {
        Row(
            modifier =
                Modifier
                    .padding(vertical = 24.dp, horizontal = 16.dp)
                    .clip(CircleShape)
                    .background(MoimTheme.colors.bg.secondary)
                    .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NetworkImage(
                modifier =
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(1.dp, MoimTheme.colors.stroke), CircleShape),
                imageUrl = user.profileUrl,
                errorImage = painterResource(R.drawable.ic_empty_user_logo),
            )

            Spacer(Modifier.width(8.dp))

            MoimText(
                text = user.nickname,
                style = MoimTheme.typography.body01.medium,
                color = MoimTheme.colors.gray.gray02,
            )
        }
    }
}
