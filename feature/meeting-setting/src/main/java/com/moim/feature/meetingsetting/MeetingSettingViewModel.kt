package com.moim.feature.meetingsetting

import androidx.lifecycle.viewModelScope
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.eventbus.actionStateIn
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.meetingsetting.model.MeetingSettingIntent
import com.moim.feature.meetingsetting.model.MeetingSettingSideEffect
import com.moim.feature.meetingsetting.model.MeetingSettingState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException

@HiltViewModel(assistedFactory = MeetingSettingViewModel.Factory::class)
class MeetingSettingViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    private val meetingEventBus: EventBus<MeetingAction>,
    private val planEventBus: EventBus<PlanAction>,
    @Assisted val meetingSettingRoute: DetailRoute.MeetingSetting,
) : MVIViewModel<MeetingSettingState, MeetingSettingSideEffect>(meetingSettingRoute.asState()) {
    private val meetingActionReceiver =
        meetingEventBus
            .action
            .actionStateIn(viewModelScope, MeetingAction.None)

    init {
        meetingActionReceiver
            .filterIsInstance<MeetingAction.MeetingUpdate>()
            .onEach { action ->
                intent { reduce { state.copy(meeting = action.meeting) } }
            }.launchIn(viewModelScope)
    }

    override suspend fun Syntax<MeetingSettingState, MeetingSettingSideEffect>.onContainerCreate() {
        val user = userRepository.getUser().first()

        reduce { state.copy(isHostUser = state.meeting.hostId == user.userId) }
    }

    override fun onIntent(intent: Intent) {
        if (intent !is MeetingSettingIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is MeetingSettingIntent.BackClick -> {
                    postSideEffect(MeetingSettingSideEffect.NavigateToBack)
                }

                is MeetingSettingIntent.MeetingEditClick -> {
                    postSideEffect(MeetingSettingSideEffect.NavigateToMeetingWrite(intent.meeting))
                }

                is MeetingSettingIntent.MeetingExitClick -> {
                    deleteMeeting()
                }

                is MeetingSettingIntent.MeetingParticipantsClick -> {
                    postSideEffect(MeetingSettingSideEffect.NavigateToMeetingParticipants(intent.viewIdType))
                }

                is MeetingSettingIntent.MeetingLeaderChangeClick -> {
                    postSideEffect(MeetingSettingSideEffect.NavigateToParticipantsForLeaderChange(intent.viewIdType))
                }

                is MeetingSettingIntent.MeetingExitDialogShow -> {
                    reduce { state.copy(isShowMeetingExitDialog = intent.isShow) }
                }

                is MeetingSettingIntent.MeetingDeleteDialogShow -> {
                    reduce { state.copy(isShowMeetingDeleteDialog = intent.isShow) }
                }
            }
        }
    }

    private suspend fun Syntax<MeetingSettingState, MeetingSettingSideEffect>.deleteMeeting() {
        val meetingId = state.meeting.id

        setLoading(true)

        try {
            meetingRepository.deleteMeeting(meetingId)

            meetingEventBus.send(MeetingAction.MeetingDelete(meetId = meetingId))
            planEventBus.send(PlanAction.PlanInvalidate())
            postSideEffect(MeetingSettingSideEffect.NavigateToBackForDelete)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<MeetingSettingState, MeetingSettingSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(MeetingSettingSideEffect.ShowToastMessage(message))
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingSettingRoute: DetailRoute.MeetingSetting): MeetingSettingViewModel
    }
}

private fun DetailRoute.MeetingSetting.asState() = MeetingSettingState(meeting = meeting)
