package com.moim.feature.plandetail.util

import com.moim.core.common.di.ApplicationScope
import com.moim.feature.plandetail.model.CommentUiModel
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

fun Flow<PlanDetailCommentAction>.planDetailCommentStateIn(
    coroutineScope: CoroutineScope
) = stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), PlanDetailCommentAction.None)

interface PlanDetailCommentViewModelDelegate {

    val commentAction: SharedFlow<PlanDetailCommentAction>

    fun createPlanDetailComment(
        actionAt: ZonedDateTime = ZonedDateTime.now(),
        commentUiModel: CommentUiModel,
    )

    fun updatePlanDetailComment(
        actionAt: ZonedDateTime = ZonedDateTime.now(),
        commentUiModel: CommentUiModel,
    )

    fun deletePlanDetailComment(
        actionAt: ZonedDateTime = ZonedDateTime.now(),
        commentId: String,
    )
}

internal class PlanDetailCommentViewModelDelegateImpl @Inject constructor(
    @ApplicationScope private val coroutineScope: CoroutineScope
) : PlanDetailCommentViewModelDelegate {

    private val _commentAction = MutableSharedFlow<PlanDetailCommentAction>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val commentAction: SharedFlow<PlanDetailCommentAction> = _commentAction

    override fun createPlanDetailComment(actionAt: ZonedDateTime, commentUiModel: CommentUiModel) {
        coroutineScope.launch {
            _commentAction.emit(PlanDetailCommentAction.CommentCreate(actionAt, commentUiModel))
        }
    }

    override fun updatePlanDetailComment(actionAt: ZonedDateTime, commentUiModel: CommentUiModel) {
        coroutineScope.launch {
            _commentAction.emit(PlanDetailCommentAction.CommentUpdate(actionAt, commentUiModel))
        }
    }

    override fun deletePlanDetailComment(actionAt: ZonedDateTime, commentId: String) {
        coroutineScope.launch {
            _commentAction.emit(PlanDetailCommentAction.CommentDelete(actionAt, commentId))
        }
    }
}

sealed class PlanDetailCommentAction(
    open val actionAt: ZonedDateTime
) {
    data class CommentCreate(
        override val actionAt: ZonedDateTime,
        val commentUiModel: CommentUiModel
    ) : PlanDetailCommentAction(actionAt)

    data class CommentUpdate(
        override val actionAt: ZonedDateTime,
        val commentUiModel: CommentUiModel
    ) : PlanDetailCommentAction(actionAt)

    data class CommentDelete(
        override val actionAt: ZonedDateTime,
        val commentId: String,
    ) : PlanDetailCommentAction(actionAt)

    data object None : PlanDetailCommentAction(ZonedDateTime.now())
}