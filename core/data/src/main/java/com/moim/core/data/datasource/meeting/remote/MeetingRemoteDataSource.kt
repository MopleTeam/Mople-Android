package com.moim.core.data.datasource.meeting.remote

import com.moim.core.data.model.MeetingResponse

internal interface MeetingRemoteDataSource {

    suspend fun getMeetings(page: Int, yearAndMonth: String, isClosed: Boolean) : List<MeetingResponse>
}