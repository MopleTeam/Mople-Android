package com.moim.core.data.datasource.plan

import com.moim.core.common.model.MeetingPlanContainer
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Place
import com.moim.core.common.model.Plan
import com.moim.core.common.model.PlanReviewContainer
import com.moim.core.common.model.User
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.remote.datasource.location.LocationRemoteDataSource
import com.moim.core.remote.datasource.plan.PlanRemoteDataSource
import com.moim.core.remote.model.PlaceResponse
import com.moim.core.remote.model.PlanResponse
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.model.asItem
import javax.inject.Inject

internal class PlanRepositoryImpl @Inject constructor(
    private val planRemoteDataSource: PlanRemoteDataSource,
    private val locationRemoteDataSource: LocationRemoteDataSource,
) : PlanRepository {
    override suspend fun getCurrentPlans(): MeetingPlanContainer = planRemoteDataSource.getCurrentPlan().asItem()

    override suspend fun getPlans(
        meetingId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Plan>> =
        planRemoteDataSource
            .getPlans(
                id = meetingId,
                cursor = cursor,
                size = size,
            ).asItem { it.map(PlanResponse::asItem) }

    override suspend fun getPlan(planId: String): Plan = planRemoteDataSource.getPlan(planId).asItem()

    override suspend fun getPlansForCalendar(date: String): PlanReviewContainer = planRemoteDataSource.getPlansForCalendar(date).asItem()

    override suspend fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String,
    ): List<Place> =
        locationRemoteDataSource
            .getSearchLocation(
                params =
                    jsonOf(
                        KEY_QUERY to keyword,
                        KEY_X_POINT to xPoint,
                        KEY_Y_POINT to yPoint,
                    ),
            ).locations
            .map(PlaceResponse::asItem)

    override suspend fun getPlanParticipants(
        planId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>> =
        planRemoteDataSource
            .getPlanParticipants(
                planId = planId,
                cursor = cursor,
                size = size,
            ).asItem {
                it.map(UserResponse::asItem)
            }

    override suspend fun joinPlan(planId: String) {
        planRemoteDataSource.joinPlan(planId)
    }

    override suspend fun leavePlan(planId: String) {
        planRemoteDataSource.leavePlan(planId)
    }

    override suspend fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String?,
        planWeatherAddress: String?,
        planDescription: String?,
        title: String,
        longitude: Double?,
        latitude: Double?,
    ): Plan =
        planRemoteDataSource
            .createPlan(
                jsonOf(
                    KEY_MEETING_ID to meetingId,
                    KEY_NAME to planName,
                    KEY_PLAN_TIME to planTime,
                    KEY_PLAN_ADDRESS to planAddress,
                    KEY_TITLE to title,
                    KEY_LOT to longitude,
                    KEY_LAT to latitude,
                    KEY_WEATHER_ADDRESS to planWeatherAddress,
                    KEY_DESCRIPTION to planDescription,
                ),
            ).asItem()

    override suspend fun updatePlan(
        planId: String,
        planName: String,
        planTime: String,
        planAddress: String?,
        planWeatherAddress: String?,
        planDescription: String?,
        title: String,
        longitude: Double?,
        latitude: Double?,
    ): Plan =
        planRemoteDataSource
            .updatePlan(
                jsonOf(
                    KEY_PLAN_ID to planId,
                    KEY_NAME to planName,
                    KEY_PLAN_TIME to planTime,
                    KEY_PLAN_ADDRESS to planAddress,
                    KEY_LOT to longitude,
                    KEY_LAT to latitude,
                    KEY_WEATHER_ADDRESS to planWeatherAddress,
                    KEY_DESCRIPTION to planDescription,
                ),
            ).asItem()

    override suspend fun deletePlan(planId: String) {
        planRemoteDataSource.deletePlan(planId)
    }

    override suspend fun reportPlan(planId: String) {
        planRemoteDataSource.reportPlan(
            jsonOf(
                KEY_PLAN_ID to planId,
                KEY_REASON to "",
            ),
        )
    }

    companion object {
        private const val KEY_QUERY = "query"
        private const val KEY_MEETING_ID = "meetId"
        private const val KEY_PLAN_ID = "planId"
        private const val KEY_PLAN_TIME = "planTime"
        private const val KEY_PLAN_ADDRESS = "planAddress"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_WEATHER_ADDRESS = "weatherAddress"
        private const val KEY_NAME = "name"
        private const val KEY_TITLE = "title"
        private const val KEY_X_POINT = "x"
        private const val KEY_Y_POINT = "y"
        private const val KEY_LOT = "lot"
        private const val KEY_LAT = "lat"
        private const val KEY_REASON = "reason"
    }
}
