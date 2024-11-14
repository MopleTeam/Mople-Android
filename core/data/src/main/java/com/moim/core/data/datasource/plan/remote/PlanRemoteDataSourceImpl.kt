package com.moim.core.data.datasource.plan.remote

import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.data.service.PlanApi
import com.moim.core.data.util.converterException
import javax.inject.Inject

internal class PlanRemoteDataSourceImpl @Inject constructor(
    private val planApi: PlanApi
) : PlanRemoteDataSource {

    override suspend fun getPlan(planId: String): MeetingPlanResponse {
        return try {
            planApi.getPlan(planId)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }
}