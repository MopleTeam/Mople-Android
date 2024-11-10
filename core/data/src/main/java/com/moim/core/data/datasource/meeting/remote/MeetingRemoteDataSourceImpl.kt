package com.moim.core.data.datasource.meeting.remote

import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.data.model.MeetingResponse
import com.moim.core.data.service.MeetingApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.converterException
import javax.inject.Inject

internal class MeetingRemoteDataSourceImpl @Inject constructor(
    private val meetingApi: MeetingApi
) : MeetingRemoteDataSource {

    override suspend fun getMeetings(): List<MeetingResponse> {
        return try {
            meetingApi.getMeetings()
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun getMeeting(meetingId: String): MeetingResponse {
        return try {
            meetingApi.getMeeting(meetingId)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun getMeetingPlans(
        page: Int,
        yearAndMonth: String,
        isClosed: Boolean
    ): List<MeetingPlanResponse> {
        return try {
            meetingApi.getMeetingPlans(page = page, yearMonth = yearAndMonth, isClosed = isClosed)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun createMeeting(
        meetingName: String,
        meetingImageUrl: String?
    ): MeetingResponse {
        return try {
            meetingApi.createMeeting(
                params = jsonOf(
                    KEY_NAME to meetingName,
                    KEY_IMAGE to meetingImageUrl
                )
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun updateMeeting(
        meetingId: String,
        meetingName: String,
        meetingImageUrl: String?
    ): MeetingResponse {
        return try {
            meetingApi.updateMeeting(
                id = meetingId,
                params = jsonOf(
                    KEY_NAME to meetingName,
                    KEY_IMAGE to meetingImageUrl
                )
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    companion object {
        private const val KEY_NAME = "name"
        private const val KEY_IMAGE = "image"
    }
}