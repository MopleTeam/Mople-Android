package com.moim.feature.alarmsetting

import com.moim.core.common.result.Result
import com.moim.core.common.result.data
import com.moim.core.data.datasource.notification.NotificationRepository
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.alarmsetting.model.AlarmSettingIntent
import com.moim.feature.alarmsetting.model.AlarmSettingSideEffect
import com.moim.feature.alarmsetting.model.AlarmSettingState
import com.moim.feature.alarmsetting.model.NotifySetting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AlarmSettingViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : MVIViewModel<AlarmSettingState, AlarmSettingSideEffect>(AlarmSettingState()) {
    override suspend fun Syntax<AlarmSettingState, AlarmSettingSideEffect>.onContainerCreate() {
        getNotifySetting()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is AlarmSettingIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is AlarmSettingIntent.BackClick -> {
                    postSideEffect(AlarmSettingSideEffect.NavigateToBack)
                }

                is AlarmSettingIntent.RefreshClick -> {
                    getNotifySetting()
                }

                is AlarmSettingIntent.PermissionRequestClick -> {
                    postSideEffect(AlarmSettingSideEffect.NavigateToSystemSetting)
                }

                is AlarmSettingIntent.MeetingNotifyChange -> {
                    setSubscribeNotify(ENABLE_MEET, intent.isCheck)
                }

                is AlarmSettingIntent.PlanNotifyChange -> {
                    setSubscribeNotify(ENABLE_PLAN, intent.isCheck)
                }

                is AlarmSettingIntent.CommentNotifyChange -> {
                    setSubscribeNotify(ENABLE_COMMENT, intent.isCheck)
                }

                is AlarmSettingIntent.MentionNotifyChange -> {
                    setSubscribeNotify(ENABLE_MENTION, intent.isCheck)
                }
            }
        }
    }

    private fun getNotifySetting() {
        intent {
            reduce { state.copy(notifySetting = Result.Loading) }

            try {
                val topics = notificationRepository.getNotificationSubscribes()

                reduce { state.copy(notifySetting = Result.Success(topics.asNotifySetting())) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                reduce { state.copy(notifySetting = Result.Error(e)) }
            }
        }
    }

    private fun setSubscribeNotify(
        topic: String,
        isCheck: Boolean,
    ) {
        intent {
            if (!state.isSuccess) return@intent

            setLoading(true)

            try {
                if (isCheck) {
                    notificationRepository.setNotificationSubscribe(topic)
                } else {
                    notificationRepository.setNotificationUnSubscribe(topic)
                }

                reduce {
                    val notifySetting = state.notifySetting.data ?: return@reduce state
                    state.copy(notifySetting = Result.Success(notifySetting.updateSubscribe(topic, isCheck)))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = if (e is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
                postSideEffect(AlarmSettingSideEffect.ShowToastMessage(message))
            } finally {
                setLoading(false)
            }
        }
    }

    private fun List<String>.asNotifySetting() =
        NotifySetting(
            isSubscribeForMeetingNotify = any { it == ENABLE_MEET },
            isSubscribeForPlanNotify = any { it == ENABLE_PLAN },
            isSubscribeForCommentNotify = any { it == ENABLE_COMMENT },
            isSubscribeForMentionNotify = any { it == ENABLE_MENTION },
        )

    private fun NotifySetting.updateSubscribe(
        topic: String,
        isCheck: Boolean,
    ) = when (topic) {
        ENABLE_MEET -> copy(isSubscribeForMeetingNotify = isCheck)
        ENABLE_PLAN -> copy(isSubscribeForPlanNotify = isCheck)
        ENABLE_COMMENT -> copy(isSubscribeForCommentNotify = isCheck)
        ENABLE_MENTION -> copy(isSubscribeForMentionNotify = isCheck)
        else -> this
    }

    companion object {
        private const val ENABLE_MEET = "MEET"
        private const val ENABLE_PLAN = "PLAN"
        private const val ENABLE_COMMENT = "REPLY"
        private const val ENABLE_MENTION = "MENTION"
    }
}
