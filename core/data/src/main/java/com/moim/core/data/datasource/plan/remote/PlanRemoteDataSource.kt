package com.moim.core.data.datasource.plan.remote

import com.moim.core.datamodel.MeetingPlanContainerResponse
import com.moim.core.datamodel.PlaceResponse
import com.moim.core.datamodel.PlanResponse

internal interface PlanRemoteDataSource {

    suspend fun getPlans(meetingId: String): List<PlanResponse>

    suspend fun getPlan(planId: String): PlanResponse

    suspend fun getCurrentPlan(): MeetingPlanContainerResponse

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

    suspend fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        longitude: Double,
        latitude: Double,
    ): PlanResponse

    suspend fun updatePlan(
        planId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        longitude: Double,
        latitude: Double,
    ): PlanResponse
}
