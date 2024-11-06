package com.moim.core.data.datasource.meeting

import com.moim.core.data.model.MeetingResponse
import kotlinx.coroutines.flow.Flow

interface MeetingRepository {

    fun getMeetings(page: Int, yearAndMonth: String, isClosed: Boolean) : Flow<List<MeetingResponse>>
}