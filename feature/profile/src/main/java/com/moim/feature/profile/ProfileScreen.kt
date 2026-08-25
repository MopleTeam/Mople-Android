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
import com.moim.core.common.result.Result
import com.moim.core.common.result.data
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
import com.moim.core.ui.view.showToast
import com.moim.feature.profile.model.ProfileIntent
import com.moim.feature.profile.model.ProfileSideEffect
import com.moim.feature.profile.model.ProfileState
import com.moim.feature.profile.ui.ProfileAuthSettingContainer
import com.moim.feature.profile.ui.ProfileImage
import com.moim.feature.profile.ui.ProfileSettingContainer
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

internal typealias OnProfileIntent = (ProfileIntent) -> Unit

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToProfileUpdate: () -> Unit,
    navigateToAlarmSetting: () -> Unit,
    navigateToThemeSetting: () -> Unit,
    navigateToPrivacyPolicy: (String) -> Unit,
    navigateToUserWithdrawalForLeaderChange: () -> Unit,
    navigateToIntro: () -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val uiState by viewModel.collectAsState()
    val modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.bg.primary, padding = padding)

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileSideEffect.NavigateToProfileUpdate -> navigateToProfileUpdate()
            is ProfileSideEffect.NavigateToAlarmSetting -> navigateToAlarmSetting()
            is ProfileSideEffect.NavigateToPrivacyPolicy -> navigateToPrivacyPolicy(NOTION_URL)
            is ProfileSideEffect.NavigateToThemeSetting -> navigateToThemeSetting()
            is ProfileSideEffect.NavigateToUserWithdrawalForLeaderChange -> navigateToUserWithdrawalForLeaderChange()
            is ProfileSideEffect.NavigateToIntro -> navigateToIntro()
            is ProfileSideEffect.ShowToastMessage -> showToast(context, sideEffect.message)
        }
    }

    when {
        uiState.isLoading -> {
            LoadingScreen(modifier)
        }

        uiState.isSuccess -> {
            ProfileScreen(
                modifier = modifier,
                uiState = uiState,
                isLoading = isLoading,
                onIntent = viewModel::onIntent,
            )
        }

        uiState.isError -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onIntent(ProfileIntent.RefreshClick) },
            )
        }
    }
}

@Composable
private fun ProfileScreen(
    modifier: Modifier = Modifier,
    uiState: ProfileState,
    isLoading: Boolean = false,
    onIntent: OnProfileIntent,
) {
    val user = uiState.user.data ?: return

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
            ProfileImage(user = user, onIntent = onIntent)
            ProfileDivider()
            ProfileSettingContainer(onIntent = onIntent)
            ProfileDivider()
            ProfileAuthSettingContainer(onIntent = onIntent)
        }
    }

    if (uiState.isShowUserLogoutDialog) {
        val dismissIntent = ProfileIntent.UserLogoutDialogShow(false)
        MoimAlertDialog(
            title = stringResource(R.string.profile_logout_title),
            onClickPositive = {
                onIntent(dismissIntent)
                onIntent(ProfileIntent.LogoutClick)
            },
            onClickNegative = { onIntent(dismissIntent) },
            onDismiss = { onIntent(dismissIntent) },
        )
    }
    if (uiState.isShowUserDeleteDialog) {
        val dismissIntent = ProfileIntent.UserDeleteDialogShow(false)
        MoimAlertDialog(
            title = stringResource(R.string.profile_user_delete_title),
            description = stringResource(R.string.profile_user_delete_description),
            positiveText = stringResource(R.string.profile_user_delete),
            positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
            onClickPositive = {
                onIntent(dismissIntent)
                onIntent(ProfileIntent.UserDeleteClick)
            },
            onClickNegative = { onIntent(dismissIntent) },
            onDismiss = { onIntent(dismissIntent) },
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
                ProfileState(
                    user =
                        Result.Success(
                            User(
                                userId = "",
                                nickname = "옥수수붕어빵",
                            ),
                        ),
                    isShowUserLogoutDialog = false,
                    isShowUserDeleteDialog = false,
                ),
            isLoading = false,
            onIntent = {},
        )
    }
}
