package com.moim.core.data.datasource.plan.remote

import com.moim.core.datamodel.MeetingPlanContainerResponse
import com.moim.core.datamodel.ParticipantContainerResponse
import com.moim.core.datamodel.PlaceResponse
import com.moim.core.datamodel.PlanResponse
import com.moim.core.datamodel.PlanReviewContainerResponse

internal interface PlanRemoteDataSource {

    suspend fun getPlans(meetingId: String): List<PlanResponse>

    suspend fun getPlan(planId: String): PlanResponse

    suspend fun getCurrentPlan(): MeetingPlanContainerResponse

    suspend fun getPlansForCalendar(date: String): PlanReviewContainerResponse

    suspend fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String
    ): List<PlaceResponse>

    suspend fun getPlanParticipants(planId: String): ParticipantContainerResponse

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

    suspend fun deletePlan(planId: String)

    suspend fun reportPlan(planId: String)
}
