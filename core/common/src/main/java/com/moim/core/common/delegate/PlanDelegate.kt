package com.moim.core.common.delegate

import com.moim.core.common.di.ApplicationScope
import com.moim.core.model.item.PlanItem
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

fun Flow<PlanAction>.planItemStateIn(
    coroutineScope: CoroutineScope
) = stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), PlanAction.None)

interface PlanItemViewModelDelegate {

    val planItemAction: SharedFlow<PlanAction>

    fun createPlanItem(actionAt: ZonedDateTime, planItem: PlanItem)

    fun updatePlanItem(actionAt: ZonedDateTime, planItem: PlanItem)

    fun deletePlanItem(actionAt: ZonedDateTime, postId: String)

    fun invalidatePlanItem(actionAt: ZonedDateTime)
}

internal class PlanItemViewModelDelegateImpl @Inject constructor(
    @ApplicationScope private val coroutineScope: CoroutineScope
) : PlanItemViewModelDelegate {

    private val _planAction = MutableSharedFlow<PlanAction>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val planItemAction: SharedFlow<PlanAction> = _planAction

    override fun createPlanItem(actionAt: ZonedDateTime, planItem: PlanItem) {
        coroutineScope.launch {
            _planAction.emit(PlanAction.PlanCreate(actionAt, planItem))
        }
    }

    override fun updatePlanItem(actionAt: ZonedDateTime, planItem: PlanItem) {
        coroutineScope.launch {
            _planAction.emit(PlanAction.PlanUpdate(actionAt, planItem))
        }
    }

    override fun deletePlanItem(actionAt: ZonedDateTime, postId: String) {
        coroutineScope.launch {
            _planAction.emit(PlanAction.PlanDelete(actionAt, postId))
        }
    }

    override fun invalidatePlanItem(actionAt: ZonedDateTime) {
        coroutineScope.launch {
            _planAction.emit(PlanAction.PlanInvalidate(actionAt))
        }
    }
}

sealed class PlanAction(
    open val actionAt: ZonedDateTime
) {
    data class PlanInvalidate(
        override val actionAt: ZonedDateTime
    ) : PlanAction(actionAt)

    data class PlanCreate(
        override val actionAt: ZonedDateTime,
        val planItem: PlanItem
    ) : PlanAction(actionAt)

    data class PlanUpdate(
        override val actionAt: ZonedDateTime,
        val planItem: PlanItem
    ) : PlanAction(actionAt)

    data class PlanDelete(
        override val actionAt: ZonedDateTime,
        val postId: String
    ) : PlanAction(actionAt)

    data object None : PlanAction(ZonedDateTime.now())
}