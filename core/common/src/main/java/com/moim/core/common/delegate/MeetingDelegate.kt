package com.moim.core.common.delegate

import com.moim.core.common.di.ApplicationScope
import com.moim.core.model.Meeting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

fun Flow<MeetingAction>.meetingStateIn(
    coroutineScope: CoroutineScope
) = stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), MeetingAction.None)

interface MeetingViewModelDelegate {

    val meetingAction: SharedFlow<MeetingAction>

    fun createMeeting(actionAt: ZonedDateTime, meeting: Meeting)

    fun updateMeeting(actionAt: ZonedDateTime, meeting: Meeting)

    fun deleteMeeting(actionAt: ZonedDateTime, meetingId: String)

    fun invalidateMeeting(actionAt: ZonedDateTime)
}

internal class MeetingViewModelDelegateImpl @Inject constructor(
    @ApplicationScope private val coroutineScope: CoroutineScope
) : MeetingViewModelDelegate {

    private val _meetingAction = MutableSharedFlow<MeetingAction>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val meetingAction: SharedFlow<MeetingAction> = _meetingAction

    override fun createMeeting(actionAt: ZonedDateTime, meeting: Meeting) {
        coroutineScope.launch {
            _meetingAction.emit(MeetingAction.MeetingCreate(actionAt, meeting))
        }
    }

    override fun updateMeeting(actionAt: ZonedDateTime, meeting: Meeting) {
        coroutineScope.launch {
            _meetingAction.emit(MeetingAction.MeetingUpdate(actionAt, meeting))
        }
    }

    override fun deleteMeeting(actionAt: ZonedDateTime, meetingId: String) {
        coroutineScope.launch {
            _meetingAction.emit(MeetingAction.MeetingDelete(actionAt, meetingId))
        }
    }

    override fun invalidateMeeting(actionAt: ZonedDateTime) {
        coroutineScope.launch {
            _meetingAction.emit(MeetingAction.MeetingInvalidate(actionAt))
        }
    }
}

sealed class MeetingAction(
    open val actionAt: ZonedDateTime
) {
    data class MeetingInvalidate(
        override val actionAt: ZonedDateTime
    ) : MeetingAction(actionAt)

    data class MeetingCreate(
        override val actionAt: ZonedDateTime,
        val meeting: Meeting
    ) : MeetingAction(actionAt)

    data class MeetingUpdate(
        override val actionAt: ZonedDateTime,
        val meeting: Meeting
    ) : MeetingAction(actionAt)

    data class MeetingDelete(
        override val actionAt: ZonedDateTime,
        val meetId: String
    ) : MeetingAction(actionAt)

    data object None : MeetingAction(ZonedDateTime.now())
}