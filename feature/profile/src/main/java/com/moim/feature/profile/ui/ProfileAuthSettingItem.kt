package com.moim.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.profile.OnProfileUiAction
import com.moim.feature.profile.ProfileUiAction

@Composable
fun ProfileAuthSettingContainer(
    modifier: Modifier = Modifier,
    onUiAction: OnProfileUiAction,
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ProfileAuthSettingItem(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onSingleClick { onUiAction(ProfileUiAction.OnShowUserLogoutDialog(true)) },
            text = stringResource(R.string.profile_logout),
        )
        ProfileAuthSettingItem(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .onSingleClick { onUiAction(ProfileUiAction.OnClickUserWithdrawal) },
            text = stringResource(R.string.profile_user_delete),
        )
    }
}

@Composable
fun ProfileAuthSettingItem(
    modifier: Modifier = Modifier,
    text: String,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        MoimText(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 20.dp),
            text = text,
            style = MoimTheme.typography.title03.medium,
            color = MoimTheme.colors.text.text01,
        )
    }
}
