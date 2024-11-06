package com.moim.core.data.datasource.meeting.remote

import com.moim.core.data.model.MeetingResponse
import com.moim.core.data.service.MeetingApi
import com.moim.core.data.util.converterException
import javax.inject.Inject

internal class MeetingRemoteDataSourceImpl @Inject constructor(
    private val meetingApi: MeetingApi
) : MeetingRemoteDataSource {

    override suspend fun getMeetings(
        page: Int,
        yearAndMonth: String,
        isClosed: Boolean
    ): List<MeetingResponse> {
        return try {
            meetingApi.getMeetings(page = page, yearMonth = yearAndMonth, isClosed = isClosed)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }
}