package com.moim.core.data.datasource.plan

import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.service.LocationApi
import com.moim.core.data.service.PlanApi
import com.moim.core.data.util.catchFlow
import com.moim.core.datamodel.PlaceResponse
import com.moim.core.datamodel.PlanResponse
import com.moim.core.model.PlanReviewContainer
import com.moim.core.model.asItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class PlanRepositoryImpl @Inject constructor(
    private val planApi: PlanApi,
    private val locationApi: LocationApi,
) : PlanRepository {

    override fun getCurrentPlans() = catchFlow {
        emit(planApi.getCurrentPlan().asItem())
    }

    override fun getPlans(meetingId: String) = catchFlow {
        emit(planApi.getPlans(meetingId).map(PlanResponse::asItem))
    }

    override fun getPlan(planId: String) = catchFlow {
        emit(planApi.getPlan(planId).asItem())
    }

    override fun getPlansForCalendar(date: String): Flow<PlanReviewContainer> = catchFlow {
        emit(planApi.getPlansForCalendar(date).asItem())
    }

    override fun getSearchPlace(keyword: String, xPoint: String, yPoint: String) = catchFlow {
        emit(
            locationApi.getSearchLocation(
                params = jsonOf(
                    KEY_QUERY to keyword,
                    KEY_X_POINT to xPoint,
                    KEY_Y_POINT to yPoint,
                )
            ).locations.map(PlaceResponse::asItem)
        )
    }

    override fun getPlanParticipants(planId: String) = catchFlow {
        val planParticipants = planApi.getPlanParticipants(planId)
        emit(planParticipants.members.map { it.asItem(planParticipants.creatorId == it.memberId) })
    }

    override fun joinPlan(planId: String) = catchFlow {
        emit(planApi.joinPlan(planId))
    }

    override fun leavePlan(planId: String) = catchFlow {
        emit(planApi.leavePlan(planId))
    }

    override fun createPlan(
        meetingId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        planWeatherAddress: String,
        title: String,
        longitude: Double,
        latitude: Double
    ) = catchFlow {
        emit(
            planApi.createPlan(
                jsonOf(
                    KEY_MEETING_ID to meetingId,
                    KEY_NAME to planName,
                    KEY_PLAN_TIME to planTime,
                    KEY_PLAN_ADDRESS to planAddress,
                    KEY_TITLE to title,
                    KEY_LOT to longitude,
                    KEY_LAT to latitude,
                    KEY_WEATHER_ADDRESS to planWeatherAddress
                )
            ).asItem()
        )
    }

    override fun updatePlan(
        planId: String,
        planName: String,
        planTime: String,
        planAddress: String,
        planWeatherAddress: String,
        title: String,
        longitude: Double,
        latitude: Double
    ) = catchFlow {
        emit(
            planApi.updatePlan(
                jsonOf(
                    KEY_PLAN_ID to planId,
                    KEY_NAME to planName,
                    KEY_PLAN_TIME to planTime,
                    KEY_PLAN_ADDRESS to planAddress,
                    KEY_LOT to longitude,
                    KEY_LAT to latitude,
                    KEY_WEATHER_ADDRESS to planWeatherAddress
                )
            ).asItem()
        )
    }

    override fun deletePlan(planId: String) = catchFlow {
        emit(planApi.deletePlan(planId))
    }

    override fun reportPlan(planId: String) = catchFlow {
        emit(
            planApi.reportPlan(
                jsonOf(
                    KEY_PLAN_ID to planId,
                    KEY_REASON to "",
                )
            )
        )
    }

    companion object {
        private const val KEY_QUERY = "query"
        private const val KEY_MEETING_ID = "meetId"
        private const val KEY_PLAN_ID = "planId"
        private const val KEY_PLAN_TIME = "planTime"
        private const val KEY_PLAN_ADDRESS = "planAddress"
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