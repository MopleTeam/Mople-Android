package com.moim.feature.alarm

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Notification
import com.moim.core.common.model.NotificationType
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.ViewIdType
import com.moim.core.data.datasource.notification.NotificationRepository
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.feature.alarm.model.AlarmUiModel
import com.moim.feature.alarm.model.asUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : BaseViewModel() {
    private var pagingJob: Job? = null

    init {
        viewModelScope.launch {
            setUiState(AlarmUiState())
            getAlarms()
        }
    }

    fun onUiAction(uiAction: AlarmUiAction) {
        when (uiAction) {
            is AlarmUiAction.OnClickBack -> {
                setUiEvent(AlarmUiEvent.NavigateToBack)
            }

            is AlarmUiAction.OnClickRefresh -> {
                val uiState = uiState.value as? AlarmUiState ?: return
                getAlarms(uiState.pagingInfo.nextCursor)
            }

            is AlarmUiAction.OnLoadNextPage -> {
                val uiState = uiState.value as? AlarmUiState ?: return
                getAlarms(uiState.pagingInfo.nextCursor)
            }

            is AlarmUiAction.OnUpdateNotificationCount -> {
                clearNotificationCount()
            }

            is AlarmUiAction.OnClickAlarm -> {
                navigateToNotifyTarget(uiAction.item)
            }
        }
    }

    private fun getAlarms(cursor: String? = null) {
        if (pagingJob.isActiveCheck()) return
        pagingJob =
            viewModelScope.launch {
                handlePagingData(
                    pagingInfo = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingInfo =
                    runCatching {
                        notificationRepository.getNotifications(
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                handlePagingData(
                    pagingInfo = pagingInfo,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private fun handlePagingData(
        pagingInfo: PaginationContainer<List<Notification>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        uiState.checkState<AlarmUiState> {
            val result =
                PagingHelper.handlePagingResult(
                    pagingData = pagingInfo,
                    isLoading = isLoading,
                    currentPagingInfo = this.pagingInfo,
                    currentItems = alarms,
                    isInitialLoad = cursor == null,
                    transform = { notifications ->
                        notifications.map { notification ->
                            notification.asUiModel()
                        }
                    },
                )

            setUiState(
                copy(
                    pagingInfo = result.pagingInfo,
                    alarms = result.items,
                ),
            )
        }
    }

    private fun clearNotificationCount() {
        viewModelScope.launch {
            runCatching {
                notificationRepository.clearNotificationCount().first()
            }
        }
    }

    private fun navigateToNotifyTarget(alarmUiModel: AlarmUiModel) {
        when (alarmUiModel.type) {
            NotificationType.MEET_NEW_MEMBER,
            NotificationType.PLAN_DELETE,
            -> {
                setUiEvent(AlarmUiEvent.NavigateToMeetingDetail(requireNotNull(alarmUiModel.meetId)))
            }

            NotificationType.COMMENT_REPLY,
            NotificationType.COMMENT_MENTION,
            NotificationType.PLAN_CREATE,
            NotificationType.PLAN_UPDATE,
            NotificationType.PLAN_REMIND,
            NotificationType.REVIEW_REMIND,
            NotificationType.REVIEW_UPDATE,
            -> {
                val viewIdType =
                    if (alarmUiModel.planId != null) {
                        ViewIdType.PlanId(requireNotNull(alarmUiModel.planId))
                    } else if (alarmUiModel.reviewId != null) {
                        ViewIdType.ReviewId(requireNotNull(alarmUiModel.reviewId))
                    } else {
                        return
                    }

                val isPlan = (alarmUiModel.planDate?.toLocalDate()?.isAfter(LocalDate.now()) == true)

                if (!isPlan && viewIdType is ViewIdType.PlanId) {
                    setUiEvent(AlarmUiEvent.NavigateToPlanDetail(ViewIdType.PostId(viewIdType.id)))
                } else {
                    setUiEvent(AlarmUiEvent.NavigateToPlanDetail(viewIdType))
                }
            }

            NotificationType.NONE -> {
                return
            }
        }
    }
}

data class AlarmUiState(
    val pagingInfo: PagingUiState = PagingUiState(),
    val alarms: List<AlarmUiModel> = emptyList(),
) : UiState

sealed interface AlarmUiAction : UiAction {
    data object OnClickBack : AlarmUiAction

    data object OnClickRefresh : AlarmUiAction

    data class OnClickAlarm(
        val item: AlarmUiModel,
    ) : AlarmUiAction

    data object OnUpdateNotificationCount : AlarmUiAction

    data object OnLoadNextPage : AlarmUiAction
}

sealed interface AlarmUiEvent : UiEvent {
    data object NavigateToBack : AlarmUiEvent

    data class NavigateToMeetingDetail(
        val meetingId: String,
    ) : AlarmUiEvent

    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : AlarmUiEvent
}
