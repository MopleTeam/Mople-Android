package com.moim.feature.participantlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.util.externalShareForUrl
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.PAGING_ERROR
import com.moim.core.ui.view.PAGING_LOADING
import com.moim.core.ui.view.isAppendLoading
import com.moim.core.ui.view.isError
import com.moim.core.ui.view.isLoading
import com.moim.core.ui.view.showToast
import com.moim.feature.participantlist.ui.ParticipantListItem
import com.moim.feature.participantlist.ui.ParticipantMeetingInviteItem

@Composable
fun ParticipantListRoute(
    padding: PaddingValues,
    viewModel: ParticipantListViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToImageViewer: (
        title: String,
        images: List<String>,
        position: Int,
        defaultImage: Int,
    ) -> Unit,
) {
    val context = LocalContext.current
    val participantListUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier =
        Modifier.containerScreen(
            backgroundColor = MoimTheme.colors.bg.primary,
            padding = padding,
        )

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is ParticipantListUiEvent.NavigateToBack -> {
                navigateToBack()
            }

            is ParticipantListUiEvent.NavigateToImageViewer -> {
                navigateToImageViewer(
                    event.userName,
                    listOf(event.userImage),
                    0,
                    R.drawable.ic_empty_user_logo,
                )
            }

            is ParticipantListUiEvent.NavigateToExternalShareUrl -> {
                context.externalShareForUrl(event.url)
            }

            is ParticipantListUiEvent.ShowToastMessage -> {
                showToast(context, event.toastMessage)
            }
        }
    }

    (participantListUiState as? ParticipantListUiState)?.let { uiState ->
        ParticipantListScreen(
            modifier = modifier,
            uiState = uiState,
            onUiAction = viewModel::onUiAction,
        )
    }
}

@Composable
fun ParticipantListScreen(
    modifier: Modifier = Modifier,
    uiState: ParticipantListUiState,
    onUiAction: (ParticipantListUiAction) -> Unit,
) {
    val participants =
        uiState.participant
            ?.collectAsLazyPagingItems(LocalLifecycleOwner.current.lifecycleScope.coroutineContext)
            ?: return

    TrackScreenViewEvent(screenName = "participant_list")
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MoimTopAppbar(
                title = stringResource(R.string.participant_list_title),
                onClickNavigate = { onUiAction(ParticipantListUiAction.OnClickBack) },
            )
            Spacer(Modifier.height(28.dp))
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MoimText(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.participant_list_title),
                    style = MoimTheme.typography.body01.medium,
                    color = MoimTheme.colors.text.text03,
                )
                MoimText(
                    text = stringResource(R.string.unit_participants_count_short, uiState.totalCount),
                    style = MoimTheme.typography.body01.medium,
                    color = MoimTheme.colors.text.text03,
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (uiState.isMeeting) {
                    item {
                        ParticipantMeetingInviteItem(
                            onUiAction = onUiAction,
                        )
                    }
                }

                items(
                    count = participants.itemCount,
                    key = participants.itemKey(),
                    contentType = participants.itemContentType(),
                ) { index ->
                    val participant = participants[index] ?: return@items
                    ParticipantListItem(
                        modifier = Modifier.animateItem(),
                        isMeeting = uiState.isMeeting,
                        participant = participant,
                        onUiAction = onUiAction,
                    )
                }

                if (participants.loadState.isAppendLoading()) {
                    item(key = PAGING_LOADING) {
                        PagingLoadingScreen(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(MoimTheme.colors.bg.primary)
                                    .animateItem(),
                        )
                    }
                }

                if (participants.loadState.isAppendLoading()) {
                    item(key = PAGING_ERROR) {
                        PagingErrorScreen(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(MoimTheme.colors.bg.primary)
                                    .animateItem(),
                            onClickRetry = participants::retry,
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut(),
            visible = participants.loadState.isLoading(),
        ) {
            PagingLoadingScreen(modifier = Modifier.fillMaxSize())
        }

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut(),
            visible = participants.loadState.isError(),
        ) {
            ErrorScreen(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MoimTheme.colors.bg.primary),
                onClickRefresh = { participants.refresh() },
            )
        }
    }
}
