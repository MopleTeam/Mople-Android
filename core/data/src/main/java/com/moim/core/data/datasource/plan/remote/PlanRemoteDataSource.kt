package com.moim.core.data.datasource.plan.remote

import com.moim.core.data.model.MeetingPlanResponse

internal interface PlanRemoteDataSource {

    suspend fun getPlan(planId: String): MeetingPlanResponse
}
