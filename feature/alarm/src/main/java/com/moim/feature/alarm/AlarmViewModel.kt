package com.moim.feature.alarm

import com.moim.core.common.model.Notification
import com.moim.core.common.model.NotificationType
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.ViewIdType
import com.moim.core.data.datasource.notification.NotificationRepository
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.PagingHelper
import com.moim.feature.alarm.model.AlarmIntent
import com.moim.feature.alarm.model.AlarmSideEffect
import com.moim.feature.alarm.model.AlarmState
import com.moim.feature.alarm.model.AlarmUiModel
import com.moim.feature.alarm.model.asUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import org.orbitmvi.orbit.syntax.Syntax
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : MVIViewModel<AlarmState, AlarmSideEffect>(AlarmState()) {
    private var pagingJob: Job? = null

    override suspend fun Syntax<AlarmState, AlarmSideEffect>.onContainerCreate() {
        getAlarms()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is AlarmIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is AlarmIntent.BackClick -> {
                    postSideEffect(AlarmSideEffect.NavigateToBack)
                }

                is AlarmIntent.RefreshClick,
                is AlarmIntent.NextPageLoad,
                -> {
                    getAlarms(state.pagingInfo.nextCursor)
                }

                is AlarmIntent.NotificationCountUpdate -> {
                    clearNotificationCount()
                }

                is AlarmIntent.AlarmClick -> {
                    navigateToNotifyTarget(intent.item)
                }
            }
        }
    }

    private fun getAlarms(cursor: String? = null) {
        if (pagingJob.isActiveCheck()) return
        pagingJob =
            intent {
                handlePagingData(
                    pagingData = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingData =
                    runCatching {
                        notificationRepository.getNotifications(
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                handlePagingData(
                    pagingData = pagingData,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private suspend fun Syntax<AlarmState, AlarmSideEffect>.handlePagingData(
        pagingData: PaginationContainer<List<Notification>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        val result =
            PagingHelper.handlePagingResult(
                pagingData = pagingData,
                isLoading = isLoading,
                currentPagingInfo = state.pagingInfo,
                currentItems = state.alarms,
                isInitialLoad = cursor == null,
                transform = { notifications ->
                    notifications.map { notification ->
                        notification.asUiModel()
                    }
                },
            )

        reduce {
            state.copy(
                pagingInfo = result.pagingInfo,
                alarms = result.items,
            )
        }
    }

    private suspend fun clearNotificationCount() {
        runCatching {
            notificationRepository.clearNotificationCount()
        }
    }

    private suspend fun Syntax<AlarmState, AlarmSideEffect>.navigateToNotifyTarget(alarmUiModel: AlarmUiModel) {
        when (alarmUiModel.type) {
            NotificationType.MEET_NEW_MEMBER,
            NotificationType.PLAN_DELETE,
            -> {
                postSideEffect(AlarmSideEffect.NavigateToMeetingDetail(requireNotNull(alarmUiModel.meetId)))
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
                    postSideEffect(AlarmSideEffect.NavigateToPlanDetail(ViewIdType.PostId(viewIdType.id)))
                } else {
                    postSideEffect(AlarmSideEffect.NavigateToPlanDetail(viewIdType))
                }
            }

            NotificationType.NONE -> {
                return
            }
        }
    }
}
