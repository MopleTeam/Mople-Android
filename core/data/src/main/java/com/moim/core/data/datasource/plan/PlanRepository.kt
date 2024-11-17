package com.moim.core.data.datasource.plan

import com.moim.core.data.model.MeetingPlanContainer
import com.moim.core.data.model.MeetingPlanResponse
import kotlinx.coroutines.flow.Flow

interface PlanRepository {

    fun getCurrentPlans() : Flow<MeetingPlanContainer>

    fun getPlan(planId: String) : Flow<MeetingPlanResponse>

    fun getPlans(
        page: Int,
        yearAndMonth: String,
        isClosed: Boolean
    ): Flow<List<MeetingPlanResponse>>
}