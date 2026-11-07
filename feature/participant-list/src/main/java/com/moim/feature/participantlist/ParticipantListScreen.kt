package com.moim.feature.participantlist

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.util.externalShareForUrl
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.showToast
import com.moim.feature.participantlist.model.ParticipantListIntent
import com.moim.feature.participantlist.model.ParticipantListSideEffect
import com.moim.feature.participantlist.model.ParticipantListState
import com.moim.feature.participantlist.ui.ParticipantListItem
import com.moim.feature.participantlist.ui.ParticipantMeetingInviteItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

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
    val uiState by viewModel.collectAsState()
    val modifier =
        Modifier.containerScreen(
            backgroundColor = MoimTheme.colors.bg.primary,
            padding = padding,
        )

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ParticipantListSideEffect.NavigateToBack -> {
                navigateToBack()
            }

            is ParticipantListSideEffect.NavigateToImageViewer -> {
                navigateToImageViewer(
                    sideEffect.userName,
                    listOf(sideEffect.userImage),
                    0,
                    R.drawable.ic_empty_user_logo,
                )
            }

            is ParticipantListSideEffect.NavigateToExternalShareUrl -> {
                context.externalShareForUrl(sideEffect.url)
            }

            is ParticipantListSideEffect.ShowToastMessage -> {
                showToast(context, sideEffect.toastMessage)
            }
        }
    }

    ParticipantListScreen(
        modifier = modifier,
        uiState = uiState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun ParticipantListScreen(
    modifier: Modifier = Modifier,
    uiState: ParticipantListState,
    onIntent: (ParticipantListIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    val paging = uiState.pagingInfo

    TrackScreenViewEvent(screenName = "participant_list")
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            MoimTopAppbar(
                title = stringResource(R.string.participant_list_title),
                onClickNavigate = { onIntent(ParticipantListIntent.BackClick) },
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
                    text = stringResource(R.string.unit_participants_count_short, paging.totalCount),
                    style = MoimTheme.typography.body01.medium,
                    color = MoimTheme.colors.text.text03,
                )
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                FadeAnimatedVisibility(uiState.isLoading) {
                    LoadingScreen()
                }

                FadeAnimatedVisibility(uiState.isError) {
                    ErrorScreen {
                        onIntent(ParticipantListIntent.RefreshClick)
                    }
                }

                FadeAnimatedVisibility(uiState.isSuccess) {
                    PaginationEffect(
                        listState = listState,
                        threshold = 3,
                        enabled = !paging.isLast && !paging.isErrorFooter,
                        onNext = { onIntent(ParticipantListIntent.NextPageLoad) },
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (uiState.isMeeting) {
                        item {
                            ParticipantMeetingInviteItem(
                                onIntent = onIntent,
                            )
                        }
                    }

                    items(
                        items = uiState.participants,
                        key = { it.userId },
                    ) { user ->
                        ParticipantListItem(
                            modifier = Modifier.animateItem(),
                            isMeeting = uiState.isMeeting,
                            participant = user,
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
                                onIntent(ParticipantListIntent.RefreshClick)
                            }
                        }
                    }
                }
            }
        }
    }
}
