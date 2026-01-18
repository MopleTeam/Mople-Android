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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.consts.NOTION_URL
import com.moim.core.common.model.User
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.showToast
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
    navigateToThemeSetting: () -> Unit,
    navigateToPrivacyPolicy: (String) -> Unit,
    navigateToIntro: () -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val profileUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.bg.primary, padding = padding)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ProfileUiEvent.NavigateToProfileUpdate -> navigateToProfileUpdate()
            is ProfileUiEvent.NavigateToAlarmSetting -> navigateToAlarmSetting()
            is ProfileUiEvent.NavigateToPrivacyPolicy -> navigateToPrivacyPolicy(NOTION_URL)
            is ProfileUiEvent.NavigateToThemeSetting -> navigateToThemeSetting()
            is ProfileUiEvent.NavigateToIntro -> navigateToIntro()
            is ProfileUiEvent.ShowToastMessage -> showToast(context, event.message)
        }
    }

    when (val uiState = profileUiState) {
        is ProfileUiState.Loading -> {
            LoadingScreen(modifier)
        }

        is ProfileUiState.Success -> {
            ProfileScreen(
                modifier = modifier,
                uiState = uiState,
                isLoading = isLoading,
                onUiAction = viewModel::onUiAction,
            )
        }

        is ProfileUiState.Error -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onUiAction(ProfileUiAction.OnClickRefresh) },
            )
        }
    }
}

@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState.Success,
    isLoading: Boolean = false,
    onUiAction: OnProfileUiAction,
) {
    TrackScreenViewEvent(screenName = "profile")
    Column(
        modifier = modifier,
    ) {
        MoimTopAppbar(
            title = stringResource(R.string.profile_title),
            isNavigationIconVisible = false,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            ProfileImage(user = uiState.user, onUiAction = onUiAction)
            ProfileDivider()
            ProfileSettingContainer(onUiAction = onUiAction)
            ProfileDivider()
            ProfileAuthSettingContainer(onUiAction = onUiAction)
        }
    }

    if (uiState.isShowUserLogoutDialog) {
        val dismissAction = ProfileUiAction.OnShowUserLogoutDialog(false)
        MoimAlertDialog(
            title = stringResource(R.string.profile_logout_title),
            onClickPositive = {
                onUiAction(dismissAction)
                onUiAction(ProfileUiAction.OnClickLogout)
            },
            onClickNegative = { onUiAction(dismissAction) },
            onDismiss = { onUiAction(dismissAction) },
        )
    }
    if (uiState.isShowUserDeleteDialog) {
        val dismissAction = ProfileUiAction.OnShowUserDeleteDialog(false)
        MoimAlertDialog(
            title = stringResource(R.string.profile_user_delete_title),
            description = stringResource(R.string.profile_user_delete_description),
            positiveText = stringResource(R.string.profile_user_delete),
            positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
            onClickPositive = {
                onUiAction(dismissAction)
                onUiAction(ProfileUiAction.OnClickUserDelete)
            },
            onClickNegative = { onUiAction(dismissAction) },
            onDismiss = { onUiAction(dismissAction) },
        )
    }

    LoadingDialog(isLoading)
}

@Composable
private fun ProfileDivider() {
    Spacer(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MoimTheme.colors.bg.secondary),
    )
}

@ThemePreviews
@Composable
private fun ProfileScreenPreview() {
    MoimTheme {
        ProfileScreen(
            uiState =
                ProfileUiState.Success(
                    user =
                        User(
                            userId = "",
                            nickname = "옥수수붕어빵",
                        ),
                    isShowUserLogoutDialog = false,
                    isShowUserDeleteDialog = false,
                ),
            isLoading = false,
            onUiAction = {},
        )
    }
}
