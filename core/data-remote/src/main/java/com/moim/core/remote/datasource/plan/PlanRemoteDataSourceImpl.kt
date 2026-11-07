package com.moim.core.remote.datasource.plan

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.model.MeetingPlanContainerResponse
import com.moim.core.remote.model.PaginationContainerResponse
import com.moim.core.remote.model.PlanResponse
import com.moim.core.remote.model.PlanReviewContainerResponse
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.util.patchJson
import com.moim.core.remote.util.postJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class PlanRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
) : PlanRemoteDataSource {
    override suspend fun getCurrentPlan(): MeetingPlanContainerResponse = client.get("plan/view").body()

    override suspend fun getPlans(
        id: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<PlanResponse>> =
        client
            .get("plan/list/$id") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun getPlansForCalendar(date: String): PlanReviewContainerResponse =
        client
            .get("plan/page") {
                parameter("date", date)
            }.body()

    override suspend fun getPlan(planId: String): PlanResponse = client.get("plan/detail/$planId").body()

    override suspend fun getPlanParticipants(
        planId: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<UserResponse>> =
        client
            .get("plan/participants/$planId") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun joinPlan(id: String) {
        client.postJson("plan/join/$id")
    }

    override suspend fun leavePlan(id: String) {
        client.delete("plan/leave/$id")
    }

    override suspend fun createPlan(params: JsonObject): PlanResponse = client.postJson("plan/create", params).body()

    override suspend fun updatePlan(params: JsonObject): PlanResponse = client.patchJson("plan/update", params).body()

    override suspend fun reportPlan(params: JsonObject) {
        client.postJson("plan/report", params)
    }

    override suspend fun deletePlan(id: String) {
        client.delete("plan/$id")
    }
}
