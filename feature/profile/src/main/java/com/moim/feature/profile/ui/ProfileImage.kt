package com.moim.feature.profile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.User
import com.moim.feature.profile.OnProfileUiAction
import com.moim.feature.profile.ProfileUiAction

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    user: User,
    onUiAction: OnProfileUiAction
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .onSingleClick { onUiAction(ProfileUiAction.OnClickProfile) },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NetworkImage(
                modifier = Modifier
                    .clip(CircleShape)
                    .border(BorderStroke(1.dp, MoimTheme.colors.stroke), CircleShape)
                    .size(80.dp),
                imageUrl = user.profileUrl,
                errorImage = painterResource(R.drawable.ic_empty_user_logo),
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoimText(
                    text = user.nickname,
                    singleLine = false,
                    style = MoimTheme.typography.title03.semiBold,
                    color = MoimTheme.colors.gray.gray01
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_pen),
                    contentDescription = "",
                    tint = MoimTheme.colors.icon
                )
            }
        }
    }
}
