package com.moim.feature.participantlist.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.participantlist.ParticipantListUiAction

@Composable
fun ParticipantMeetingInviteItem(
    modifier: Modifier = Modifier,
    onUiAction: (ParticipantListUiAction) -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .onSingleClick { onUiAction(ParticipantListUiAction.OnClickMeetingInvite) }
                .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MoimTheme.colors.bg.primary),
        ) {
            Icon(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .size(18.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                contentDescription = "",
                tint = MoimTheme.colors.primary.primary,
            )
        }

        Spacer(Modifier.width(8.dp))

        MoimText(
            text = stringResource(R.string.common_invite),
            style = MoimTheme.typography.title03.medium,
            color = MoimTheme.colors.primary.primary,
        )
    }
}
