package com.moim.core.data.datasource.plan.remote

import com.moim.core.data.model.MeetingPlanContainer
import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.data.model.PlaceResponse
import com.moim.core.data.service.LocationApi
import com.moim.core.data.service.PlanApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.converterException
import javax.inject.Inject

internal class PlanRemoteDataSourceImpl @Inject constructor(
    private val planApi: PlanApi,
    private val locationApi: LocationApi
) : PlanRemoteDataSource {

    override suspend fun getPlan(planId: String): MeetingPlanResponse {
        return try {
            planApi.getPlan(planId)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun getCurrentPlan(): MeetingPlanContainer {
        return try {
            planApi.getCurrentPlan()
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun getPlans(
        page: Int,
        yearAndMonth: String,
        isClosed: Boolean
    ): List<MeetingPlanResponse> {
        return try {
            planApi.getPlans(page = page, yearMonth = yearAndMonth, isClosed = isClosed)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String
    ): List<PlaceResponse> {
        return try {
            locationApi.getSearchLocation(
                params = jsonOf(
                    KEY_QUERY to keyword,
                    KEY_X_POINT to xPoint,
                    KEY_Y_POINT to yPoint,
                )
            ).locations
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    companion object {
        private const val KEY_QUERY = "query"
        private const val KEY_X_POINT = "x"
        private const val KEY_Y_POINT = "y"
    }
}