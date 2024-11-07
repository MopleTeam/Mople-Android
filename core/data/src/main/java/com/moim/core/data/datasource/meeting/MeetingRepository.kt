package com.moim.core.data.datasource.meeting

import com.moim.core.data.model.MeetingPlanResponse
import kotlinx.coroutines.flow.Flow

interface MeetingRepository {

    fun getMeetingPlans(page: Int, yearAndMonth: String, isClosed: Boolean) : Flow<List<MeetingPlanResponse>>
}