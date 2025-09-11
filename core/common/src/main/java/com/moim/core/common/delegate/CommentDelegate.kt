package com.moim.core.common.delegate

import com.moim.core.common.di.ApplicationScope
import com.moim.core.common.model.item.CommentUiModel
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

fun Flow<CommentAction>.commentStateIn(
    coroutineScope: CoroutineScope
) = stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), CommentAction.None)

interface CommentViewModelDelegate {

    val commentAction: SharedFlow<CommentAction>

    fun createPlanComment(
        actionAt: ZonedDateTime = ZonedDateTime.now(),
        commentUiModel: CommentUiModel,
    )

    fun updatePlanComment(
        actionAt: ZonedDateTime = ZonedDateTime.now(),
        commentUiModel: CommentUiModel,
    )

    fun deletePlanComment(
        actionAt: ZonedDateTime = ZonedDateTime.now(),
        commentId: String,
    )
}

internal class CommentViewModelDelegateImpl @Inject constructor(
    @ApplicationScope private val coroutineScope: CoroutineScope
) : CommentViewModelDelegate {

    private val _commentAction = MutableSharedFlow<CommentAction>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val commentAction: SharedFlow<CommentAction> = _commentAction

    override fun createPlanComment(actionAt: ZonedDateTime, commentUiModel: CommentUiModel) {
        coroutineScope.launch {
            _commentAction.emit(CommentAction.CommentCreate(actionAt, commentUiModel))
        }
    }

    override fun updatePlanComment(actionAt: ZonedDateTime, commentUiModel: CommentUiModel) {
        coroutineScope.launch {
            _commentAction.emit(CommentAction.CommentUpdate(actionAt, commentUiModel))
        }
    }

    override fun deletePlanComment(actionAt: ZonedDateTime, commentId: String) {
        coroutineScope.launch {
            _commentAction.emit(CommentAction.CommentDelete(actionAt, commentId))
        }
    }
}

sealed class CommentAction(
    open val actionAt: ZonedDateTime
) {
    data class CommentCreate(
        override val actionAt: ZonedDateTime,
        val commentUiModel: CommentUiModel
    ) : CommentAction(actionAt)

    data class CommentUpdate(
        override val actionAt: ZonedDateTime,
        val commentUiModel: CommentUiModel
    ) : CommentAction(actionAt)

    data class CommentDelete(
        override val actionAt: ZonedDateTime,
        val commentId: String,
    ) : CommentAction(actionAt)

    data object None : CommentAction(ZonedDateTime.now())
}