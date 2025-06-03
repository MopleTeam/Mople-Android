package com.moim.feature.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.profile.BuildConfig
import com.moim.feature.profile.OnProfileUiAction
import com.moim.feature.profile.ProfileUiAction

@Composable
fun ProfileSettingContainer(
    modifier: Modifier = Modifier,
    onUiAction: OnProfileUiAction
) {
    val prefix = if (BuildConfig.DEBUG) "[DEV] " else ""

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        ProfileSettingItem(
            text = stringResource(R.string.profile_alarm),
            onClick = { onUiAction(ProfileUiAction.OnClickAlarmSetting) }
        )
        ProfileSettingItem(
            text = stringResource(R.string.profile_privacy_policy),
            onClick = { onUiAction(ProfileUiAction.OnClickPrivacyPolicy) }
        )
        ProfileSettingItem(
            text = stringResource(R.string.profile_version),
            subText = prefix + BuildConfig.VERSION_NAME
        )
    }
}

@Composable
fun ProfileSettingItem(
    modifier: Modifier = Modifier,
    text: String,
    subText: String? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .onSingleClick(enabled = subText == null, onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoimText(
            modifier = Modifier.weight(1f),
            text = text,
            singleLine = false,
            style = MoimTheme.typography.title03.medium,
            color = MoimTheme.colors.gray.gray01
        )

        Spacer(Modifier.width(4.dp))

        if (subText == null) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_next),
                contentDescription = "",
                tint = MoimTheme.colors.icon
            )
        } else {
            MoimText(
                text = subText,
                singleLine = false,
                style = MoimTheme.typography.title03.medium,
                color = MoimTheme.colors.gray.gray06
            )
        }
    }
}