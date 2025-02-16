package com.moim.core.data.datasource.meeting.remote

import com.moim.core.datamodel.MeetingResponse
import com.moim.core.datamodel.ParticipantContainerResponse


internal interface MeetingRemoteDataSource {

    suspend fun getMeetings(): List<MeetingResponse>

    suspend fun getMeeting(meetingId: String): MeetingResponse

    suspend fun getMeetingParticipants(meetingId: String): ParticipantContainerResponse

    suspend fun createMeeting(meetingName: String, meetingImageUrl: String?): MeetingResponse

    suspend fun updateMeeting(meetingId: String, meetingName: String, meetingImageUrl: String?): MeetingResponse

    suspend fun deleteMeeting(meetingId: String)
}