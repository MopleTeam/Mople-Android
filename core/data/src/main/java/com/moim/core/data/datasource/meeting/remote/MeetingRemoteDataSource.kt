package com.moim.core.data.datasource.meeting.remote

import com.moim.core.datamodel.MeetingResponse


internal interface MeetingRemoteDataSource {

    suspend fun getMeetings(): List<MeetingResponse>

    suspend fun getMeeting(meetingId: String): MeetingResponse

    suspend fun createMeeting(meetingName: String, meetingImageUrl: String?): MeetingResponse

    suspend fun updateMeeting(meetingId: String, meetingName: String, meetingImageUrl: String?): MeetingResponse

    suspend fun deleteMeeting(meetingId: String)
}