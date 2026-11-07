package com.moim.core.remote.datasource.plan

import com.moim.core.remote.model.MeetingPlanContainerResponse
import com.moim.core.remote.model.PaginationContainerResponse
import com.moim.core.remote.model.PlanResponse
import com.moim.core.remote.model.PlanReviewContainerResponse
import com.moim.core.remote.model.UserResponse
import kotlinx.serialization.json.JsonObject

interface PlanRemoteDataSource {
    suspend fun getCurrentPlan(): MeetingPlanContainerResponse

    suspend fun getPlans(
        id: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<PlanResponse>>

    suspend fun getPlansForCalendar(date: String): PlanReviewContainerResponse

    suspend fun getPlan(planId: String): PlanResponse

    suspend fun getPlanParticipants(
        planId: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<UserResponse>>

    suspend fun joinPlan(id: String)

    suspend fun leavePlan(id: String)

    suspend fun createPlan(params: JsonObject): PlanResponse

    suspend fun updatePlan(params: JsonObject): PlanResponse

    suspend fun reportPlan(params: JsonObject)

    suspend fun deletePlan(id: String)
}
