package com.moim.feature.planwrite.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
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
import com.moim.core.ui.view.PAGING_ERROR
import com.moim.core.ui.view.PAGING_LOADING
import com.moim.core.ui.view.isAppendError
import com.moim.core.ui.view.isAppendLoading
import com.moim.core.ui.view.isError
import com.moim.core.ui.view.isLoading
import com.moim.feature.planwrite.OnPlanWriteUiAction
import com.moim.feature.planwrite.PlanWriteUiAction
import com.moim.feature.planwrite.model.MeetingUiModel
import kotlinx.coroutines.launch

@Composable
fun PlanWriteMeetingsDialog(
    modifier: Modifier = Modifier,
    meetings: LazyPagingItems<MeetingUiModel>,
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
            onUiAction = onUiAction,
        )
    }
}

@Composable
fun PlanWriteMeetingsTopAppbar(
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
            color = MoimTheme.colors.gray.gray02,
        )

        MoimIconButton(
            iconRes = R.drawable.ic_close,
            onClick = onClick,
        )
    }
}

@Composable
fun PlanWriteMeetingsScreen(
    modifier: Modifier = Modifier,
    meetings: LazyPagingItems<MeetingUiModel>,
    onUiAction: OnPlanWriteUiAction,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 60.dp),
        ) {
            items(
                count = meetings.itemCount,
                key = meetings.itemKey(),
                contentType = meetings.itemContentType(),
            ) { index ->
                val meetingUiModel = meetings[index] ?: return@items
                PlanWriteMeetingInfo(
                    meeting = meetingUiModel.meeting,
                    isSelected = meetingUiModel.isSelected,
                    onUiAction = onUiAction,
                )
            }

            if (meetings.loadState.isAppendLoading()) {
                item(key = PAGING_LOADING) {
                    PagingLoadingScreen(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(MoimTheme.colors.white)
                                .animateItem(),
                    )
                }
            }

            if (meetings.loadState.isAppendError()) {
                item(key = PAGING_ERROR) {
                    PagingErrorScreen(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .background(MoimTheme.colors.white)
                                .animateItem(),
                        onClickRetry = meetings::retry,
                    )
                }
            }

            item {
                AnimatedVisibility(
                    visible = meetings.loadState.isLoading(),
                ) {
                    PagingLoadingScreen()
                }
            }

            item {
                AnimatedVisibility(
                    modifier = Modifier.fillMaxWidth(),
                    visible = meetings.loadState.isError(),
                ) {
                    PagingErrorScreen(
                        modifier = modifier,
                        onClickRetry = meetings::refresh,
                    )
                }
            }
        }
    }
}

@Composable
fun PlanWriteMeetingInfo(
    modifier: Modifier = Modifier,
    meeting: Meeting,
    isSelected: Boolean,
    onUiAction: OnPlanWriteUiAction,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color = if (isSelected) MoimTheme.colors.bg.input else MoimTheme.colors.white)
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
