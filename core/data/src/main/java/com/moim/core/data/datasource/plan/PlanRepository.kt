package com.moim.core.data.datasource.plan

import com.moim.core.data.model.MeetingPlanResponse
import kotlinx.coroutines.flow.Flow

interface PlanRepository {

    fun getCurrentPlans()

    fun getPlan(planId: String) : Flow<MeetingPlanResponse>
}