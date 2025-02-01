package com.moim.core.data.datasource.plan

import com.moim.core.model.MeetingPlanContainer
import com.moim.core.model.Place
import com.moim.core.model.Plan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {

    fun getCurrentPlans(): Flow<MeetingPlanContainer>

    fun getPlan(planId: String): Flow<Plan>

    fun getPlans(meetingId: String): Flow<List<Plan>>

    fun getPlansForCalendar(
        page: Int,
        yearAndMonth: String,
        isClosed: Boolean
    ): Flow<List<Plan>>

    fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String
    ): Flow<List<Place>>

    fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        longitude: Double,
        latitude: Double,
    ): Flow<Plan>

    fun updatePlan(
        planId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        longitude: Double,
        latitude: Double,
    ): Flow<Plan>

    fun deletePlan(
        planId: String
    ) : Flow<Unit>

    fun reportPlan(
        planId: String
    ) : Flow<Unit>
}