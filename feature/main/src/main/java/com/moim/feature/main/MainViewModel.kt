package com.moim.feature.main

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.ViewIdType
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.feature.main.model.MainIntent
import com.moim.feature.main.model.MainSideEffect
import com.moim.feature.main.model.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    private val meetingEventBus: EventBus<MeetingAction>,
    private val planEventBus: EventBus<PlanAction>,
) : MVIViewModel<MainState, MainSideEffect>(MainState()) {
    init {
        userRepository
            .getTheme()
            .onEach { theme -> intent { reduce { state.copy(theme = theme) } } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: Intent) {
        if (intent !is MainIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is MainIntent.PlanNotifyReceive -> {
                    planEventBus.send(PlanAction.PlanInvalidate())
                    postSideEffect(MainSideEffect.NavigateToPlanDetail(ViewIdType.PlanId(intent.planId)))
                }

                is MainIntent.ReviewNotifyReceive -> {
                    planEventBus.send(PlanAction.PlanInvalidate())
                    postSideEffect(MainSideEffect.NavigateToPlanDetail(ViewIdType.ReviewId(intent.reviewId)))
                }

                is MainIntent.MeetingNotifyReceive -> {
                    meetingEventBus.send(MeetingAction.MeetingInvalidate())
                    postSideEffect(MainSideEffect.NavigateToMeetingDetail(intent.meetingId))
                }

                is MainIntent.MeetingInviteReceive -> {
                    joinMeeting(intent.meetCode)
                }
            }
        }
    }

    private suspend fun Syntax<MainState, MainSideEffect>.joinMeeting(meetCode: String) {
        try {
            val meeting = meetingRepository.joinMeeting(meetCode)

            meetingEventBus.send(MeetingAction.MeetingInvalidate())
            postSideEffect(MainSideEffect.NavigateToMeetingDetail(meeting.id))
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            return
        }
    }
}
