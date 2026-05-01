package com.moim.feature.planwrite.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.Meeting
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimBottomSheetDialog
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.FadeAnimatedVisibility
import com.moim.core.ui.view.PaginationEffect
import com.moim.core.ui.view.PagingUiState
import com.moim.feature.planwrite.OnPlanWriteUiAction
import com.moim.feature.planwrite.PlanWriteUiAction
import com.moim.feature.planwrite.model.MeetingUiModel
import kotlinx.coroutines.launch

@Composable
fun PlanWriteMeetingsDialog(
    modifier: Modifier = Modifier,
    meetings: List<MeetingUiModel>,
    pagingInfo: PagingUiState,
    onUiAction: OnPlanWriteUiAction,
) {
    val dismissAction = PlanWriteUiAction.OnShowMeetingsDialog(false)
    val sheetState: SheetState = rememberModalBottomSheetState(true)
    val coroutineScope = rememberCoroutineScope()

    MoimBottomSheetDialog(
        modifier = modifier,
        onDismiss = {
            coroutineScope
                .launch { sheetState.hide() }
                .invokeOnCompletion { onUiAction(dismissAction) }
        },
    ) {
        PlanWriteMeetingsTopAppbar(
            onClick = { onUiAction(dismissAction) },
        )

        PlanWriteMeetingsScreen(
            meetings = meetings,
            pagingInfo = pagingInfo,
            onUiAction = onUiAction,
        )
    }
}

@Composable
private fun PlanWriteMeetingsTopAppbar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 12.dp, top = 22.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoimText(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.plan_write_meeting_select),
            style = MoimTheme.typography.title02.semiBold,
        )

        MoimIconButton(
            iconRes = R.drawable.ic_close,
            onClick = onClick,
        )
    }
}

@Composable
private fun PlanWriteMeetingsScreen(
    modifier: Modifier = Modifier,
    meetings: List<MeetingUiModel>,
    pagingInfo: PagingUiState,
    onUiAction: OnPlanWriteUiAction,
) {
    val configuration = LocalConfiguration.current
    val sheetHeight = (configuration.screenHeightDp * 0.6f).dp

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(sheetHeight),
        contentAlignment = Alignment.Center,
    ) {
        FadeAnimatedVisibility(pagingInfo.isLoading) {
            PagingLoadingScreen()
        }

        FadeAnimatedVisibility(pagingInfo.isError) {
            PagingErrorScreen(
                onClickRetry = { onUiAction(PlanWriteUiAction.OnLoadNextMeetingsPage) },
            )
        }

        FadeAnimatedVisibility(pagingInfo.isSuccess) {
            MeetingsPagingList(
                meetings = meetings,
                pagingInfo = pagingInfo,
                onUiAction = onUiAction,
            )
        }
    }
}

@Composable
private fun MeetingsPagingList(
    meetings: List<MeetingUiModel>,
    pagingInfo: PagingUiState,
    onUiAction: OnPlanWriteUiAction,
) {
    val listState = rememberLazyListState()

    PaginationEffect(
        listState = listState,
        threshold = 3,
        enabled = !pagingInfo.isLast && !pagingInfo.isErrorFooter,
        onNext = { onUiAction(PlanWriteUiAction.OnLoadNextMeetingsPage) },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(bottom = 60.dp),
        ) {
            items(
                items = meetings,
                key = { it.meeting.id },
            ) { meetingUiModel ->
                PlanWriteMeetingInfo(
                    modifier = Modifier.animateItem(),
                    meeting = meetingUiModel.meeting,
                    isSelected = meetingUiModel.isSelected,
                    onUiAction = onUiAction,
                )
            }

            item {
                if (pagingInfo.isLoadingFooter) {
                    PagingLoadingScreen(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(MoimTheme.colors.bg.primary)
                                .animateItem(),
                    )
                }
            }

            item {
                if (pagingInfo.isErrorFooter) {
                    PagingErrorScreen(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(MoimTheme.colors.bg.primary)
                                .animateItem(),
                        onClickRetry = { onUiAction(PlanWriteUiAction.OnLoadNextMeetingsPage) },
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanWriteMeetingInfo(
    modifier: Modifier = Modifier,
    meeting: Meeting,
    isSelected: Boolean,
    onUiAction: OnPlanWriteUiAction,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = if (isSelected) MoimTheme.colors.bg.input else MoimTheme.colors.bg.primary)
                .onSingleClick {
                    onUiAction(PlanWriteUiAction.OnClickPlanMeeting(meeting))
                    onUiAction(PlanWriteUiAction.OnShowMeetingsDialog(false))
                }.padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NetworkImage(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .border(BorderStroke(1.dp, MoimTheme.colors.stroke), shape = RoundedCornerShape(6.dp))
                    .size(22.dp),
            imageUrl = meeting.imageUrl,
            errorImage = painterResource(R.drawable.ic_empty_meeting),
        )

        Spacer(Modifier.width(8.dp))

        MoimText(
            modifier = Modifier.fillMaxWidth(),
            text = meeting.name,
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray02,
        )
    }
}
