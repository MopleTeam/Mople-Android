package com.moim.core.data.datasource.meeting

import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.mapper.asItem
import com.moim.core.data.util.catchFlow
import com.moim.core.datamodel.MeetingResponse
import com.moim.core.model.Meeting
import com.moim.core.network.service.MeetingApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MeetingRepositoryImpl @Inject constructor(
    private val meetingApi: MeetingApi,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource
) : MeetingRepository {

    override fun getMeetings() = catchFlow {
        emit(meetingApi.getMeetings().map(MeetingResponse::asItem))
    }

    override fun getMeeting(meetingId: String) = catchFlow {
        emit(meetingApi.getMeeting(meetingId).asItem())
    }

    override fun getMeetingInviteCode(meetingId: String) = catchFlow {
        emit(meetingApi.getMeetingInviteCode(meetingId))
    }

    override fun getMeetingParticipants(meetingId: String) = catchFlow {
        val meetingParticipants = meetingApi.getMeetingParticipants(meetingId)
        emit(meetingParticipants.members.map { it.asItem(meetingParticipants.creatorId == it.memberId) })
    }

    override fun createMeeting(meetingName: String, meetingImageUrl: String?): Flow<Meeting> = catchFlow {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(meetingImageUrl, "meeting")
        emit(meetingApi.createMeeting(jsonOf(KEY_NAME to meetingName, KEY_IMAGE to uploadImageUrl)).asItem())
    }

    override fun updateMeeting(meetingId: String, meetingName: String, meetingImageUrl: String?): Flow<Meeting> = catchFlow {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(meetingImageUrl, "meeting")

        emit(
            meetingApi.updateMeeting(
                id = meetingId,
                params = jsonOf(
                    KEY_NAME to meetingName,
                    KEY_IMAGE to uploadImageUrl
                )
            ).asItem()
        )
    }

    override fun joinMeeting(code: String): Flow<Meeting> = catchFlow {
        emit(meetingApi.joinMeeting(code).asItem())
    }

    override fun deleteMeeting(meetingId: String): Flow<Unit> = catchFlow {
        emit(meetingApi.deleteMeeting(meetingId))
    }

    companion object {
        private const val KEY_NAME = "name"
        private const val KEY_IMAGE = "image"
    }
}