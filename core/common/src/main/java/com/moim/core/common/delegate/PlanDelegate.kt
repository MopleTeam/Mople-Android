package com.moim.core.common.delegate

import com.moim.core.common.di.ApplicationScope
import com.moim.core.model.Plan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

interface PlanViewModelDelegate {

    val planAction: SharedFlow<PlanAction>

    fun createPlan(actionAt: ZonedDateTime, plan: Plan)

    fun updatePlan(actionAt: ZonedDateTime, plan: Plan)

    fun deletePlan(actionAt: ZonedDateTime, planId: String)

    fun invalidatePlan(actionAt: ZonedDateTime)
}

internal class PlanViewModelDelegateImpl @Inject constructor(
    @ApplicationScope private val coroutineScope: CoroutineScope
) : PlanViewModelDelegate {

    private val _planAction = MutableSharedFlow<PlanAction>(
        extraBufferCapacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val planAction: SharedFlow<PlanAction> = _planAction

    override fun createPlan(actionAt: ZonedDateTime, plan: Plan) {
        coroutineScope.launch {
            _planAction.emit(PlanAction.PlanCreate(actionAt, plan))
        }
    }

    override fun updatePlan(actionAt: ZonedDateTime, plan: Plan) {
        coroutineScope.launch {
            _planAction.emit(PlanAction.PlanUpdate(actionAt, plan))
        }
    }

    override fun deletePlan(actionAt: ZonedDateTime, planId: String) {
        coroutineScope.launch {
            _planAction.emit(PlanAction.PlanDelete(actionAt, planId))
        }
    }

    override fun invalidatePlan(actionAt: ZonedDateTime) {
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
        val plan: Plan
    ) : PlanAction(actionAt)

    data class PlanUpdate(
        override val actionAt: ZonedDateTime,
        val plan: Plan
    ) : PlanAction(actionAt)

    data class PlanDelete(
        override val actionAt: ZonedDateTime,
        val planId: String
    ) : PlanAction(actionAt)

    data object None : PlanAction(ZonedDateTime.now())
}