package com.moim.core.data.datasource.meeting

import com.moim.core.data.datasource.meeting.remote.MeetingRemoteDataSource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class MeetingRepositoryImpl @Inject constructor(
    private val remoteDataSource: MeetingRemoteDataSource
) : MeetingRepository {

    override fun getMeetings(page: Int, yearAndMonth: String, isClosed: Boolean) = flow {
        emit(remoteDataSource.getMeetings(page, yearAndMonth, isClosed))
    }
}