package com.moim.feature.participantlistforleaderchange

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimPrimaryButton
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.showToast
import com.moim.feature.participantlistforleaderchange.model.ParticipantListForLeaderChangeIntent
import com.moim.feature.participantlistforleaderchange.model.ParticipantListForLeaderChangeSideEffect
import com.moim.feature.participantlistforleaderchange.model.ParticipantListForLeaderChangeState
import com.moim.feature.participantlistforleaderchange.ui.ParticipantChangeLeaderDialog
import com.moim.feature.participantlistforleaderchange.ui.ParticipantListItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ParticipantListForLeaderChangeRoute(
    padding: PaddingValues,
    viewModel: ParticipantListForLeaderChangeViewModel = hiltViewModel(),
    navigateToBack: (isPopBack: Boolean) -> Unit,
    navigateToImageViewer: (
        title: String,
        images: List<String>,
        position: Int,
        defaultImage: Int,
    ) -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.collectAsState()
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ParticipantListForLeaderChangeSideEffect.NavigateToBack -> {
                navigateToBack(false)
            }

            is ParticipantListForLeaderChangeSideEffect.NavigateToExit -> {
                navigateToBack(true)
            }

            is ParticipantListForLeaderChangeSideEffect.NavigateToImageViewer -> {
                val user = sideEffect.user
                navigateToImageViewer(
                    user.nickname,
                    listOf(user.profileUrl),
                    0,
                    R.drawable.ic_empty_user_logo,
                )
            }

            is ParticipantListForLeaderChangeSideEffect.ShowCompletedMessage -> {
                showToast(context, context.getString(R.string.participant_list_for_leader_change_completed))
            }

            is ParticipantListForLeaderChangeSideEffect.ShowErrorMessage -> {
                showToast(context, context.getString(R.string.common_error_disconnection))
            }
        }
    }

    ParticipantListForLeaderChangeScreen(
        modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary),
        uiState = uiState,
        isLoading = isLoading,
        onIntent = viewModel::onIntent,
    )
}

@Composable
private fun ParticipantListForLeaderChangeScreen(
    modifier: Modifier = Modifier,
    uiState: ParticipantListForLeaderChangeState,
    isLoading: Boolean,
    onIntent: (ParticipantListForLeaderChangeIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    val paging = uiState.pagingInfo

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .imePadding(),
    ) {
        MoimTopAppbar(
            title = stringResource(R.string.participant_list_for_leader_change_title),
            onClickNavigate = {
                onIntent(ParticipantListForLeaderChangeIntent.BackClick)
            },
        )
        MoimTextField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp)),
            textFieldState = uiState.keywordState,
            hintText = stringResource(R.string.participant_list_for_leader_change_search_hint),
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = MoimTheme.colors.text.text04,
                )
            },
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            FadeAnimatedVisibility(uiState.isLoading) {
                LoadingScreen()
            }

            FadeAnimatedVisibility(uiState.isError) {
                ErrorScreen {
                    onIntent(ParticipantListForLeaderChangeIntent.RefreshClick)
                }
            }

            FadeAnimatedVisibility(uiState.isSuccess && uiState.users.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_user_search),
                        contentDescription = null,
                        tint = MoimTheme.colors.icon,
                    )

                    Text(
                        text = stringResource(R.string.participant_list_for_leader_change_empty),
                        style = MoimTheme.typography.body01.medium,
                        color = MoimTheme.colors.gray.gray04,
                    )
                }
            }

            FadeAnimatedVisibility(uiState.isSuccess && uiState.users.isNotEmpty()) {
                PaginationEffect(
                    listState = listState,
                    threshold = 3,
                    enabled = !paging.isLast && !paging.isErrorFooter,
                    onNext = { onIntent(ParticipantListForLeaderChangeIntent.NextPageLoad) },
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(top = 28.dp, bottom = 90.dp),
                ) {
                    items(
                        items = uiState.users,
                        key = { it.user.userId },
                    ) { data ->
                        ParticipantListItem(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .animateItem(),
                            participant = data,
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
                            ) {
                                onIntent(ParticipantListForLeaderChangeIntent.RefreshClick)
                            }
                        }
                    }
                }
            }

            if (uiState.isLoading || uiState.isError) return

            androidx.compose.animation.AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = uiState.selectedUser != null,
                enter =
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec =
                            tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing,
                            ),
                    ),
                exit =
                    slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight },
                    ),
            ) {
                MoimPrimaryButton(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp, bottom = 28.dp),
                    onClick = { onIntent(ParticipantListForLeaderChangeIntent.ChangeLeaderDialogShow(true)) },
                    text = stringResource(R.string.participant_list_for_leader_change),
                )
            }
        }
    }

    if (uiState.isShowChangeUserDialog && uiState.selectedUser != null) {
        ParticipantChangeLeaderDialog(
            user = requireNotNull(uiState.selectedUser),
            onIntent = onIntent,
        )
    }

    LoadingDialog(isLoading)
}
