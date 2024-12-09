package com.moim.core.data.datasource.plan.remote

import com.moim.core.data.service.LocationApi
import com.moim.core.data.service.PlanApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.converterException
import com.moim.core.datamodel.MeetingPlanContainer
import com.moim.core.datamodel.PlaceResponse
import com.moim.core.datamodel.PlanResponse
import javax.inject.Inject

internal class PlanRemoteDataSourceImpl @Inject constructor(
    private val planApi: PlanApi,
    private val locationApi: LocationApi
) : PlanRemoteDataSource {

    override suspend fun getPlans(meetingId: String): List<PlanResponse> {
        return try {
            planApi.getPlans(meetingId)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun getPlan(planId: String): PlanResponse {
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

    override suspend fun getPlansForCalendar(
        page: Int,
        yearAndMonth: String,
        isClosed: Boolean
    ): List<PlanResponse> {
        return try {
            planApi.getPlansForCalendar(page = page, yearMonth = yearAndMonth, isClosed = isClosed)
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

    override suspend fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        longitude: Double,
        latitude: Double
    ): PlanResponse {
        return try {
            planApi.createPlan(
                jsonOf(
                    KEY_MEETING_ID to meetingId,
                    KEY_NAME to planName,
                    KEY_PLAN_TIME to planTime,
                    KEY_PLAN_ADDRESS to planAddress,
                    KEY_LOT to longitude,
                    KEY_LAT to latitude,
                    KEY_WEATHER_ADDRESS to planAddress
                )
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun updatePlan(
        planId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        longitude: Double,
        latitude: Double
    ): PlanResponse {
        return try {
            planApi.updatePlan(
                jsonOf(
                    KEY_PLAN_ID to planId,
                    KEY_NAME to planName,
                    KEY_PLAN_TIME to planTime,
                    KEY_PLAN_ADDRESS to planAddress,
                    KEY_LOT to longitude,
                    KEY_LAT to latitude,
                    KEY_WEATHER_ADDRESS to planAddress
                )
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    companion object {
        private const val KEY_QUERY = "query"
        private const val KEY_MEETING_ID = "meetId"
        private const val KEY_PLAN_ID = "planId"
        private const val KEY_PLAN_TIME = "planTime"
        private const val KEY_PLAN_ADDRESS = "planAddress"
        private const val KEY_WEATHER_ADDRESS = "weatherAddress"
        private const val KEY_NAME = "name"
        private const val KEY_X_POINT = "x"
        private const val KEY_Y_POINT = "y"
        private const val KEY_LOT = "lot"
        private const val KEY_LAT = "lat"
    }
}