package com.moim.feature.profileupdate.ui

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
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.feature.profileupdate.OnProfileUpdateUiAction
import com.moim.feature.profileupdate.ProfileUpdateUiAction


@Composable
fun ProfileUpdateImage(
    modifier: Modifier = Modifier,
    profileUrl: String?,
    onUiAction: OnProfileUpdateUiAction = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            NetworkImage(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(100))
                    .align(Alignment.Center)
                    .onSingleClick { onUiAction(ProfileUpdateUiAction.OnShowProfileEditDialog(true)) },
                imageUrl = profileUrl,
                errorImage =  painterResource(R.drawable.ic_profile),
            )

            Icon(
                modifier = Modifier.align(Alignment.BottomEnd),
                imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}