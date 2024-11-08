package com.moim.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.color_F7F7F8
import com.moim.core.designsystem.theme.color_FFFFFF
import com.moim.feature.profile.ui.ProfileAuthSettingContainer
import com.moim.feature.profile.ui.ProfileImage
import com.moim.feature.profile.ui.ProfileSettingContainer

internal typealias OnProfileUiAction = (ProfileUiAction) -> Unit

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToProfileUpdate: () -> Unit,
    navigateToAlarmSetting: () -> Unit,
    navigateToPrivacyPolicy: () -> Unit,
    navigateToIntro: () -> Unit,
) {
    val context = LocalContext.current
    val profileUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ProfileUiEvent.NavigateToProfileUpdate -> navigateToProfileUpdate()
            is ProfileUiEvent.NavigateToAlarmSetting -> navigateToAlarmSetting()
            is ProfileUiEvent.NavigateToPrivacyPolicy -> navigateToPrivacyPolicy()
            is ProfileUiEvent.NavigateToIntro -> navigateToIntro()
            is ProfileUiEvent.ShowToastMessage -> showToast(context, event.messageRes)
        }
    }

    when (val uiState = profileUiState) {
        is ProfileUiState.Loading -> LoadingScreen(modifier)

        is ProfileUiState.Success -> ProfileScreen(
            modifier = Modifier.containerScreen(backgroundColor = color_FFFFFF, padding = padding),
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )

        is ProfileUiState.Error -> ErrorScreen(
            modifier = modifier,
            onClickRefresh = { viewModel.onUiAction(ProfileUiAction.OnClickRefresh) }
        )
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState.Success,
    isLoading: Boolean = false,
    onUiAction: OnProfileUiAction,
) {
    Column(
        modifier = modifier
    ) {
        MoimTopAppbar(
            title = stringResource(R.string.profile_title),
            navigationIcon = {}
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ProfileImage(user = uiState.user, onUiAction = onUiAction)
            ProfileDivider()
            ProfileSettingContainer(onUiAction = onUiAction)
            ProfileDivider()
            ProfileAuthSettingContainer(onUiAction = onUiAction)
        }
    }

    LoadingDialog(isLoading)
}

@Composable
fun ProfileDivider() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(color_F7F7F8)
    )
}