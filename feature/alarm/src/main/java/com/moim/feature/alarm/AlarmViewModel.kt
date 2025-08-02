package com.moim.feature.alarm

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.data.datasource.notification.NotificationRepository
import com.moim.core.domain.usecase.GetNotificationsUseCase
import com.moim.core.model.Notification
import com.moim.core.model.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    getNotificationsUseCase: GetNotificationsUseCase,
) : BaseViewModel() {

    val notifications = getNotificationsUseCase().cachedIn(viewModelScope)

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

    private fun navigateToNotifyTarget(notification: Notification) {
        when (notification.type) {
            NotificationType.MEET_NEW_MEMBER,
            NotificationType.PLAN_DELETE -> {
                setUiEvent(AlarmUiEvent.NavigateToMeetingDetail(requireNotNull(notification.meetId)))
            }

            NotificationType.PLAN_CREATE,
            NotificationType.PLAN_UPDATE,
            NotificationType.PLAN_REMIND,
            NotificationType.REVIEW_REMIND,
            NotificationType.REVIEW_UPDATE -> {
                val postId = notification.planId ?: notification.reviewId ?: return
                val isPlan = notification.planId != null
                setUiEvent(AlarmUiEvent.NavigateToPlanDetail(postId, isPlan))
            }

            NotificationType.NONE -> return
        }
    }
}

sealed interface AlarmUiAction : UiAction {
    data object OnClickBack : AlarmUiAction
    data object OnClickRefresh : AlarmUiAction
    data class OnClickAlarm(val notification: Notification) : AlarmUiAction
    data object OnUpdateNotificationCount : AlarmUiAction
}

sealed interface AlarmUiEvent : UiEvent {
    data object NavigateToBack : AlarmUiEvent
    data class NavigateToMeetingDetail(val meetingId: String) : AlarmUiEvent
    data class NavigateToPlanDetail(val postId: String, val isPlan: Boolean) : AlarmUiEvent
    data object RefreshPagingData : AlarmUiEvent
}