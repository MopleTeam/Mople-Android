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
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
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
import com.moim.core.designsystem.theme.moimTextFieldColors
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.showToast
import com.moim.feature.participantlistforleaderchange.ui.ParticipantChangeLeaderDialog
import com.moim.feature.participantlistforleaderchange.ui.ParticipantListItem

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
    val participantListForLeaderUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ParticipantListForLeaderChangeUiEvent.NavigateToBack -> {
                navigateToBack(false)
            }

            is ParticipantListForLeaderChangeUiEvent.NavigateToExit -> {
                navigateToBack(true)
            }

            is ParticipantListForLeaderChangeUiEvent.NavigateToImageViewer -> {
                val user = event.user
                navigateToImageViewer(
                    user.nickname,
                    listOf(user.profileUrl),
                    0,
                    R.drawable.ic_empty_user_logo,
                )
            }

            is ParticipantListForLeaderChangeUiEvent.ShowCompletedMessage -> {
                showToast(context, context.getString(R.string.participant_list_for_leader_change_completed))
            }

            is ParticipantListForLeaderChangeUiEvent.ShowErrorMessage -> {
                showToast(context, context.getString(R.string.common_error_disconnection))
            }
        }
    }

    (participantListForLeaderUiState as? ParticipantListForLeaderChangeUiState)?.let { uiState ->
        ParticipantListForLeaderChangeScreen(
            modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary),
            uiState = uiState,
            isLoading = isLoading,
            keywordState = viewModel.keywordFieldState,
            onUiAction = viewModel::onUiAction,
        )
    }
}

@Composable
private fun ParticipantListForLeaderChangeScreen(
    modifier: Modifier = Modifier,
    uiState: ParticipantListForLeaderChangeUiState,
    isLoading: Boolean,
    keywordState: TextFieldState,
    onUiAction: (ParticipantListForLeaderChangeUiAction) -> Unit,
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
            title = stringResource(R.string.participant_list_for_leader_title),
            onClickNavigate = {
                onUiAction(ParticipantListForLeaderChangeUiAction.OnClickBack)
            },
        )
        MoimTextField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp)),
            textFieldState = keywordState,
            textFieldColors = moimTextFieldColors(textColor = MoimTheme.colors.text.text04),
            hintText = stringResource(R.string.participant_list_for_leader_search_hint),
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
            FadeAnimatedVisibility(paging.isLoading) {
                LoadingScreen()
            }

            FadeAnimatedVisibility(paging.isError) {
                ErrorScreen {
                    onUiAction(ParticipantListForLeaderChangeUiAction.OnRefreshClick)
                }
            }

            FadeAnimatedVisibility(!paging.isLoading && !paging.isError) {
                PaginationEffect(
                    listState = listState,
                    threshold = 3,
                    enabled = !paging.isLast && !paging.isErrorFooter,
                    onNext = { onUiAction(ParticipantListForLeaderChangeUiAction.OnNextPageLoad) },
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
                            onUiAction = onUiAction,
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
                                onUiAction(ParticipantListForLeaderChangeUiAction.OnRefreshClick)
                            }
                        }
                    }
                }
            }

            if (paging.isLoading || paging.isError) return

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
                    onClick = { onUiAction(ParticipantListForLeaderChangeUiAction.ShowChangeLeaderDialog(true)) },
                    text = stringResource(R.string.participant_list_for_leader_change),
                )
            }
        }
    }

    if (uiState.isShowChangeUserDialog && uiState.selectedUser != null) {
        ParticipantChangeLeaderDialog(
            user = requireNotNull(uiState.selectedUser),
            onUiAction = onUiAction,
        )
    }

    LoadingDialog(isLoading)
}
