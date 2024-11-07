package com.moim.core.data.datasource.meeting

import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.data.model.MeetingResponse
import kotlinx.coroutines.flow.Flow

interface MeetingRepository {

    fun getMeeting() : Flow<List<MeetingResponse>>

    fun getMeetingPlans(page: Int, yearAndMonth: String, isClosed: Boolean) : Flow<List<MeetingPlanResponse>>
}