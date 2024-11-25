package com.moim.core.data.datasource.plan.remote

import com.moim.core.data.model.PlaceResponse
import com.moim.core.data.model.MeetingPlanContainer
import com.moim.core.data.model.PlanResponse

internal interface PlanRemoteDataSource {

    suspend fun getPlans(meetingId: String): List<PlanResponse>

    suspend fun getPlan(planId: String): PlanResponse

    suspend fun getCurrentPlan(): MeetingPlanContainer

    suspend fun getPlansForCalendar(
        page: Int,
        yearAndMonth: String,
        isClosed: Boolean
    ): List<PlanResponse>

    suspend fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String
    ): List<PlaceResponse>
}
