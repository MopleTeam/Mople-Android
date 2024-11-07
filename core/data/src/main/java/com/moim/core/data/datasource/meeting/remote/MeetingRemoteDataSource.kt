package com.moim.core.data.datasource.meeting.remote

import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.data.model.MeetingResponse

internal interface MeetingRemoteDataSource {

    suspend fun getMeeting() : List<MeetingResponse>

    suspend fun getMeetingPlans(page: Int, yearAndMonth: String, isClosed: Boolean) : List<MeetingPlanResponse>
}