package com.moim.core.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.model.Meeting
import timber.log.Timber
import java.time.ZonedDateTime
import javax.inject.Inject

class GetMeetingsUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository
) {
    var loadedAt: ZonedDateTime = ZonedDateTime.now()

    operator fun invoke(params: Params = Params()) = Pager(
        config = PagingConfig(pageSize = params.size)
    ) {
        object : PagingSource<String, Meeting>() {

            init {
                loadedAt = ZonedDateTime.now()
            }

            override fun getRefreshKey(state: PagingState<String, Meeting>): String? = null

            override suspend fun load(loadParams: LoadParams<String>): LoadResult<String, Meeting> {
                val page = loadParams.key ?: ""
                return try {
                    val meetingContainer = meetingRepository.getMeetings(
                        cursor = page,
                        size = params.size
                    )
                    val nextCursor = meetingContainer.cursorPage.nextCursor

                    LoadResult.Page(
                        data = meetingContainer.content,
                        prevKey = null,
                        nextKey = if (meetingContainer.cursorPage.isNext && meetingContainer.cursorPage.size >= params.size) nextCursor else null
                    )
                } catch (e: Exception) {
                    Timber.e("[GetMeetingsUseCase] error $e")
                    LoadResult.Error(e)
                }
            }
        }
    }.flow

    data class Params(
        val size: Int = 30,
    )
}