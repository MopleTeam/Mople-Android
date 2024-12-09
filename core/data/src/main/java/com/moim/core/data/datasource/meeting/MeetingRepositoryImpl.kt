package com.moim.core.data.datasource.meeting

import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.datasource.meeting.remote.MeetingRemoteDataSource
import com.moim.core.datamodel.MeetingResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class MeetingRepositoryImpl @Inject constructor(
    private val remoteDataSource: MeetingRemoteDataSource,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource
) : MeetingRepository {

    override fun getMeetings() = flow {
        emit((remoteDataSource.getMeetings()))
    }

    override fun getMeeting(meetingId: String) = flow {
        emit(remoteDataSource.getMeeting(meetingId))
    }

    override fun createMeeting(meetingName: String, meetingImageUrl: String?): Flow<MeetingResponse> = flow {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(meetingImageUrl, "meeting")
        emit(remoteDataSource.createMeeting(meetingName, uploadImageUrl))
    }

    override fun updateMeeting(meetingId: String, meetingName: String, meetingImageUrl: String?): Flow<MeetingResponse> = flow {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(meetingImageUrl, "meeting")
        emit(remoteDataSource.updateMeeting(meetingId, meetingName, uploadImageUrl))
    }

    override fun deleteMeeting(meetingId: String): Flow<Unit> = flow {
        emit(remoteDataSource.deleteMeeting(meetingId))
    }
}