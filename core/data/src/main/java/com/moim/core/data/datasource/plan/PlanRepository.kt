package com.moim.core.data.datasource.plan

import com.moim.core.data.model.PlaceResponse
import com.moim.core.data.model.MeetingPlanContainer
import com.moim.core.data.model.PlanResponse
import kotlinx.coroutines.flow.Flow

interface PlanRepository {

    fun getCurrentPlans() : Flow<MeetingPlanContainer>

    fun getPlan(planId: String) : Flow<PlanResponse>

    fun getPlans(meetingId: String) : Flow<List<PlanResponse>>

    fun getPlansForCalendar(
        page: Int,
        yearAndMonth: String,
        isClosed: Boolean
    ): Flow<List<PlanResponse>>

    fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String
    ): Flow<List<PlaceResponse>>
}