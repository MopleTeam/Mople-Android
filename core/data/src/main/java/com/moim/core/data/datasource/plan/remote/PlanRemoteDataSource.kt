package com.moim.core.data.datasource.plan.remote

import com.moim.core.data.model.PlaceResponse
import com.moim.core.data.model.MeetingPlanContainer
import com.moim.core.data.model.MeetingPlanResponse

internal interface PlanRemoteDataSource {

    suspend fun getPlan(planId: String): MeetingPlanResponse

    suspend fun getCurrentPlan(): MeetingPlanContainer

    suspend fun getPlans(
        page: Int,
        yearAndMonth: String,
        isClosed: Boolean
    ): List<MeetingPlanResponse>

    suspend fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String
    ): List<PlaceResponse>
}
