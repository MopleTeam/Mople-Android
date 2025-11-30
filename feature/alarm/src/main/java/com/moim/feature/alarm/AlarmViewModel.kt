package com.moim.feature.alarm

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.moim.core.common.model.Notification
import com.moim.core.common.model.NotificationType
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.notification.NotificationRepository
import com.moim.core.domain.usecase.GetNotificationsUseCase
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    getNotificationsUseCase: GetNotificationsUseCase,
) : BaseViewModel() {

    val notifications = getNotificationsUseCase().cachedIn(viewModelScope)
    val totalCount = getNotificationTotalCount()
        .filterIsInstance<Result.Success<Int>>()
        .mapLatest { it.data }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun onUiAction(uiAction: AlarmUiAction) {
        when (uiAction) {
            is AlarmUiAction.OnClickBack -> setUiEvent(AlarmUiEvent.NavigateToBack)
            is AlarmUiAction.OnClickRefresh -> setUiEvent(AlarmUiEvent.RefreshPagingData)
            is AlarmUiAction.OnUpdateNotificationCount -> clearNotificationCount()
            is AlarmUiAction.OnClickAlarm -> navigateToNotifyTarget(uiAction.notification)
        }
    }

    private fun clearNotificationCount() {
        viewModelScope.launch {
            runCatching {
                notificationRepository.clearNotificationCount().first()
            }
        }
    }

    private fun getNotificationTotalCount() = flow<Int> {
        emit(
            notificationRepository.getNotifications(
                cursor = "",
                size = 1,
            ).totalCount
        )
    }.asResult()

    private fun navigateToNotifyTarget(notification: Notification) {
        when (notification.type) {
            NotificationType.MEET_NEW_MEMBER,
            NotificationType.PLAN_DELETE -> {
                setUiEvent(AlarmUiEvent.NavigateToMeetingDetail(requireNotNull(notification.meetId)))
            }

            NotificationType.COMMENT_REPLY,
            NotificationType.COMMENT_MENTION,
            NotificationType.PLAN_CREATE,
            NotificationType.PLAN_UPDATE,
            NotificationType.PLAN_REMIND,
            NotificationType.REVIEW_REMIND,
            NotificationType.REVIEW_UPDATE -> {
                val viewIdType = if (notification.planId != null) {
                    ViewIdType.PlanId(requireNotNull(notification.planId))
                } else if (notification.reviewId != null) {
                    ViewIdType.ReviewId(requireNotNull(notification.reviewId))
                } else {
                    return
                }

                val isPlan = (notification.planDate?.toLocalDate()?.isAfter(LocalDate.now()) == true)

                if (!isPlan && viewIdType is ViewIdType.PlanId) {
                    setUiEvent(AlarmUiEvent.NavigateToPlanDetail(ViewIdType.PostId(viewIdType.id)))
                } else {
                    setUiEvent(AlarmUiEvent.NavigateToPlanDetail(viewIdType))
                }
            }

            NotificationType.NONE -> return
        }
    }
}

sealed interface AlarmUiAction : UiAction {
    data object OnClickBack : AlarmUiAction

    data object OnClickRefresh : AlarmUiAction

    data class OnClickAlarm(
        val notification: Notification
    ) : AlarmUiAction

    data object OnUpdateNotificationCount : AlarmUiAction
}

sealed interface AlarmUiEvent : UiEvent {
    data object NavigateToBack : AlarmUiEvent

    data class NavigateToMeetingDetail(
        val meetingId: String
    ) : AlarmUiEvent

    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType
    ) : AlarmUiEvent

    data object RefreshPagingData : AlarmUiEvent
}