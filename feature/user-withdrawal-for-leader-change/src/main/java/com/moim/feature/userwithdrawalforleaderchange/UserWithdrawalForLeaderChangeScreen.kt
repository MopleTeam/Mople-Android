package com.moim.feature.userwithdrawalforleaderchange

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.model.ViewIdType
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimScaffold
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.ui.util.decimalFormatString
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.showToast
import com.moim.feature.userwithdrawalforleaderchange.model.UserWithdrawalForLeaderChangeIntent
import com.moim.feature.userwithdrawalforleaderchange.model.UserWithdrawalForLeaderChangeSideEffect
import com.moim.feature.userwithdrawalforleaderchange.model.UserWithdrawalForLeaderChangeState
import com.moim.feature.userwithdrawalforleaderchange.ui.MeetingItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun UserWithdrawalForLeaderChangeRoute(
    padding: PaddingValues,
    viewModel: UserWithdrawalForLeaderChangeViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToExit: () -> Unit,
    navigateToParticipantsForLeaderChange: (meetId: ViewIdType.MeetId) -> Unit,
) {
    val context = LocalContext.current
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)
    val uiState by viewModel.collectAsState()
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is UserWithdrawalForLeaderChangeSideEffect.NavigateToBack -> {
                navigateToBack()
            }

            is UserWithdrawalForLeaderChangeSideEffect.NavigateToParticipantsForLeaderChange -> {
                navigateToParticipantsForLeaderChange(ViewIdType.MeetId(sideEffect.meetId))
            }

            is UserWithdrawalForLeaderChangeSideEffect.NavigateToExit -> {
                navigateToExit()
            }

            is UserWithdrawalForLeaderChangeSideEffect.ShowServerErrorMessage -> {
                showToast(context, R.string.common_error_disconnection)
            }

            is UserWithdrawalForLeaderChangeSideEffect.ShowNetworkErrorMessage -> {
                showToast(context, R.string.common_error_network)
            }
        }
    }

    UserWithdrawalForLeaderChangeScreen(
        uiState = uiState,
        isLoading = isLoading,
        modifier = modifier,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun UserWithdrawalForLeaderChangeScreen(
    uiState: UserWithdrawalForLeaderChangeState,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onIntent: (UserWithdrawalForLeaderChangeIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    val paging = uiState.pagingInfo

    MoimScaffold(
        modifier = modifier,
        topBar = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MoimTheme.colors.bg.primary),
            ) {
                MoimTopAppbar(
                    onClickNavigate = {
                        onIntent(UserWithdrawalForLeaderChangeIntent.BackClick)
                    },
                )
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                    text = stringResource(R.string.user_withdrawal_for_leader_change_title),
                    style = MoimTheme.typography.heading.bold,
                    color = MoimTheme.colors.text.text01,
                )
            }
        },
        content = { padding ->
            val contentModifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)

            Box(
                modifier = contentModifier,
                contentAlignment = Alignment.Center,
            ) {
                FadeAnimatedVisibility(uiState.isLoading) {
                    LoadingScreen()
                }

                FadeAnimatedVisibility(uiState.isError) {
                    ErrorScreen {
                        onIntent(UserWithdrawalForLeaderChangeIntent.RefreshClick)
                    }
                }

                FadeAnimatedVisibility(uiState.isSuccess) {
                    PaginationEffect(
                        listState = listState,
                        threshold = 3,
                        enabled = !paging.isLast && !paging.isErrorFooter,
                        onNext = { onIntent(UserWithdrawalForLeaderChangeIntent.NextPageLoad) },
                    )

                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(MoimTheme.colors.bg.secondary)
                                .padding(horizontal = 20.dp),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(top = 28.dp, bottom = 90.dp),
                    ) {
                        item {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                MoimText(
                                    modifier = Modifier.weight(1f),
                                    text = stringResource(R.string.user_withdrawal_for_leader_change_meeting),
                                    style = MoimTheme.typography.body01.medium,
                                    color = MoimTheme.colors.text.text01,
                                )

                                MoimText(
                                    text = stringResource(R.string.unit_count, paging.totalCount.decimalFormatString()),
                                    style = MoimTheme.typography.body01.medium,
                                    color = MoimTheme.colors.text.text01,
                                )
                            }
                        }

                        items(
                            items = uiState.meetings,
                            key = { meeting -> meeting.id },
                        ) { meeting ->
                            MeetingItem(
                                modifier = Modifier.animateItem(),
                                meeting = meeting,
                                onIntent = onIntent,
                            )
                        }

                        item {
                            if (paging.isLoadingFooter) {
                                PagingLoadingScreen(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .animateItem(),
                                )
                            }
                        }

                        item {
                            if (paging.isErrorFooter) {
                                PagingErrorScreen(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .animateItem(),
                                    backgroundColor = MoimTheme.colors.bg.secondary,
                                ) {
                                    onIntent(UserWithdrawalForLeaderChangeIntent.RefreshClick)
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(MoimTheme.colors.bg.secondary)
                        .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                MoimPrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.user_withdrawal_for_leader_change_exit),
                    onClick = { onIntent(UserWithdrawalForLeaderChangeIntent.UserDeleteDialogShow(true)) },
                    buttonColors =
                        moimButtomColors().copy(
                            containerColor = MoimTheme.colors.secondary,
                            contentColor = MoimTheme.colors.bg.primary,
                        ),
                )
            }
        },
    )

    LoadingDialog(isLoading)

    UserDeleteDialog(
        isShow = uiState.isShowExitDialog,
        onIntent = onIntent,
    )
}

@Composable
private fun UserDeleteDialog(
    isShow: Boolean,
    onIntent: (UserWithdrawalForLeaderChangeIntent) -> Unit,
) {
    if (!isShow) return
    val dismissIntent = UserWithdrawalForLeaderChangeIntent.UserDeleteDialogShow(false)
    MoimAlertDialog(
        title = stringResource(R.string.user_withdrawal_for_leader_change_dialog_title),
        positiveText = stringResource(R.string.common_positive),
        negativeText = stringResource(R.string.common_cancel),
        onClickPositive = {
            onIntent(dismissIntent)
            onIntent(UserWithdrawalForLeaderChangeIntent.UserDeleteClick)
        },
        onClickNegative = { onIntent(dismissIntent) },
        onDismiss = { onIntent(dismissIntent) },
    )
}
