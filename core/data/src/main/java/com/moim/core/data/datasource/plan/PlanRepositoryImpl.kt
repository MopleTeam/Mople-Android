package com.moim.core.data.datasource.plan

import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.Plan
import com.moim.core.common.model.PlanReviewContainer
import com.moim.core.common.model.User
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.datasource.plan.PlanRepositoryImpl.Companion.KEY_DESCRIPTION
import com.moim.core.data.util.catchFlow
import com.moim.core.remote.model.PlaceResponse
import com.moim.core.remote.model.PlanResponse
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.model.asItem
import com.moim.core.remote.service.LocationApi
import com.moim.core.remote.service.PlanApi
import com.moim.core.remote.util.converterException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class PlanRepositoryImpl @Inject constructor(
    private val planApi: PlanApi,
    private val locationApi: LocationApi,
) : PlanRepository {
    override fun getCurrentPlans() =
        catchFlow {
            emit(planApi.getCurrentPlan().asItem())
        }

    override suspend fun getPlans(
        meetingId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<Plan>> =
        try {
            planApi
                .getPlans(
                    id = meetingId,
                    cursor = cursor,
                    size = size,
                ).asItem { it.map(PlanResponse::asItem) }
        } catch (e: Exception) {
            throw converterException(e)
        }

    override fun getPlan(planId: String) =
        catchFlow {
            emit(planApi.getPlan(planId).asItem())
        }

    override fun getPlansForCalendar(date: String): Flow<PlanReviewContainer> =
        catchFlow {
            emit(planApi.getPlansForCalendar(date).asItem())
        }

    override fun getSearchPlace(
        keyword: String,
        xPoint: String,
        yPoint: String,
    ) = catchFlow {
        emit(
            locationApi
                .getSearchLocation(
                    params =
                        jsonOf(
                            KEY_QUERY to keyword,
                            KEY_X_POINT to xPoint,
                            KEY_Y_POINT to yPoint,
                        ),
                ).locations
                .map(PlaceResponse::asItem),
        )
    }

    override suspend fun getPlanParticipants(
        planId: String,
        cursor: String,
        size: Int,
    ): PaginationContainer<List<User>> =
        try {
            planApi
                .getPlanParticipants(
                    planId = planId,
                    cursor = cursor,
                    size = size,
                ).asItem {
                    it.map(UserResponse::asItem)
                }
        } catch (e: Exception) {
            throw converterException(e)
        }

    override fun joinPlan(planId: String) =
        catchFlow {
            emit(planApi.joinPlan(planId))
        }

    override fun leavePlan(planId: String) =
        catchFlow {
            emit(planApi.leavePlan(planId))
        }

    override fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String?,
        planWeatherAddress: String?,
        planDescription: String?,
        title: String,
        longitude: Double?,
        latitude: Double?,
    ) = catchFlow {
        emit(
            planApi
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
                ).asItem(),
        )
    }

    override fun updatePlan(
        planId: String,
        planName: String,
        planTime: String,
        planAddress: String?,
        planWeatherAddress: String?,
        planDescription: String?,
        title: String,
        longitude: Double?,
        latitude: Double?,
    ) = catchFlow {
        emit(
            planApi
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
                ).asItem(),
        )
    }

    override fun deletePlan(planId: String) =
        catchFlow {
            emit(planApi.deletePlan(planId))
        }

    override fun reportPlan(planId: String) =
        catchFlow {
            emit(
                planApi.reportPlan(
                    jsonOf(
                        KEY_PLAN_ID to planId,
                        KEY_REASON to "",
                    ),
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
