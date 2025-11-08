package com.moim.core.ui.eventbus

import com.moim.core.common.model.Meeting
import com.moim.core.common.model.item.CommentUiModel
import com.moim.core.common.model.item.PlanItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.ZonedDateTime

sealed interface EventAction {
    val actionAt: ZonedDateTime
}

fun <T : EventAction> SharedFlow<T>.actionStateIn(
    coroutineScope: CoroutineScope,
    initialValue: T
) = this.stateIn(
    scope = coroutineScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = initialValue
)

sealed class CommentAction(
    override val actionAt: ZonedDateTime
) : EventAction {
    data class CommentCreate(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
        val commentUiModel: CommentUiModel
    ) : CommentAction(actionAt)

    data class CommentUpdate(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
        val commentUiModel: CommentUiModel
    ) : CommentAction(actionAt)

    data class CommentDelete(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
        val commentId: String,
    ) : CommentAction(actionAt)

    data object None : CommentAction(ZonedDateTime.now())
}

sealed class MeetingAction(
    override val actionAt: ZonedDateTime
) : EventAction {
    data class MeetingInvalidate(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
    ) : MeetingAction(actionAt)

    data class MeetingCreate(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
        val meeting: Meeting
    ) : MeetingAction(actionAt)

    data class MeetingUpdate(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
        val meeting: Meeting
    ) : MeetingAction(actionAt)

    data class MeetingDelete(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
        val meetId: String
    ) : MeetingAction(actionAt)

    data object None : MeetingAction(ZonedDateTime.now())
}

sealed class PlanAction(
    override val actionAt: ZonedDateTime
) : EventAction {
    data class PlanInvalidate(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
    ) : PlanAction(actionAt)

    data class PlanCreate(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
        val planItem: PlanItem
    ) : PlanAction(actionAt)

    data class PlanUpdate(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
        val planItem: PlanItem
    ) : PlanAction(actionAt)

    data class PlanDelete(
        override val actionAt: ZonedDateTime = ZonedDateTime.now(),
        val postId: String
    ) : PlanAction(actionAt)

    data object None : PlanAction(ZonedDateTime.now())
}