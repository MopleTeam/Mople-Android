package com.moim.core.data.datasource.meeting

import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.datasource.meeting.remote.MeetingRemoteDataSource
import com.moim.core.datamodel.MeetingResponse
import com.moim.core.model.Meeting
import com.moim.core.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class MeetingRepositoryImpl @Inject constructor(
    private val remoteDataSource: MeetingRemoteDataSource,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource
) : MeetingRepository {

    override fun getMeetings() = flow {
        emit((remoteDataSource.getMeetings().map(MeetingResponse::asItem)))
    }

    override fun getMeeting(meetingId: String) = flow {
        emit(remoteDataSource.getMeeting(meetingId).asItem())
    }

    override fun getMeetingParticipants(meetingId: String) = flow {
        val meetingParticipants = remoteDataSource.getMeetingParticipants(meetingId)
        emit(meetingParticipants.members.map { it.asItem(meetingParticipants.creatorId == it.memberId) })
    }

    override fun createMeeting(meetingName: String, meetingImageUrl: String?): Flow<Meeting> = flow {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(meetingImageUrl, "meeting")
        emit(remoteDataSource.createMeeting(meetingName, uploadImageUrl).asItem())
    }

    override fun updateMeeting(meetingId: String, meetingName: String, meetingImageUrl: String?): Flow<Meeting> = flow {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(meetingImageUrl, "meeting")
        emit(remoteDataSource.updateMeeting(meetingId, meetingName, uploadImageUrl).asItem())
    }

    override fun deleteMeeting(meetingId: String): Flow<Unit> = flow {
        emit(remoteDataSource.deleteMeeting(meetingId))
    }
}