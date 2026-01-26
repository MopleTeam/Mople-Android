package com.moim.feature.userwithdrawalforleaderchange.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.Meeting
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimCard
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.userwithdrawalforleaderchange.UserWithdrawalForLeaderChangeUiAction

@Composable
fun MeetingItem(
    modifier: Modifier = Modifier,
    meeting: Meeting,
    onUiAction: (UserWithdrawalForLeaderChangeUiAction) -> Unit = {},
) {
    MoimCard(
        modifier = modifier,
        onClick = { onUiAction(UserWithdrawalForLeaderChangeUiAction.OnClickMeeting(meeting.id)) },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NetworkImage(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(BorderStroke(1.dp, MoimTheme.colors.stroke), shape = RoundedCornerShape(12.dp))
                            .size(56.dp),
                    imageUrl = meeting.imageUrl,
                    errorImage = painterResource(R.drawable.ic_empty_meeting),
                )

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MoimText(
                            modifier = Modifier.weight(1f, false),
                            text = meeting.name,
                            style = MoimTheme.typography.title03.semiBold,
                            color = MoimTheme.colors.text.text01,
                        )

                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_crown),
                            contentDescription = "",
                            tint = Color.Unspecified,
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_meeting),
                            contentDescription = "",
                            tint = MoimTheme.colors.icon,
                        )

                        MoimText(
                            text = stringResource(R.string.unit_participants_count_short, meeting.memberCount),
                            style = MoimTheme.typography.body02.medium,
                            color = MoimTheme.colors.text.text03,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MoimTheme.colors.tertiary),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MoimText(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = stringResource(R.string.user_withdrawal_for_leader_change),
                    style = MoimTheme.typography.body01.semiBold,
                    color = MoimTheme.colors.gray.gray03,
                )
            }
        }
    }
}
