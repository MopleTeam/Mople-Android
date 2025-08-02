package com.moim.core.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.moim.core.common.di.IoDispatcher
import com.moim.core.data.datasource.notification.NotificationRepository
import com.moim.core.model.Notification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    operator fun invoke(params: Params = Params()) = Pager(
        config = PagingConfig(pageSize = params.size)
    ) {
        object : PagingSource<String, Notification>() {
            override fun getRefreshKey(state: PagingState<String, Notification>): String? = null

            override suspend fun load(loadParams: LoadParams<String>): LoadResult<String, Notification> {
                val page = loadParams.key ?: ""

                return try {
                    val notificationContainer = notificationRepository.getNotifications(
                        cursor = page,
                        size = params.size
                    )
                    val nextCursor = notificationContainer.cursorPage.nextCursor

                    LoadResult.Page(
                        data = notificationContainer.content,
                        prevKey = null,
                        nextKey = if (notificationContainer.cursorPage.isNext && notificationContainer.cursorPage.size >= params.size) nextCursor else null
                    )
                } catch (e: Exception) {
                    Timber.e("[GetNotificationsUseCase] error $e")
                    LoadResult.Error(e)
                }
            }
        }
    }.flow.flowOn(ioDispatcher)

    data class Params(
        val size: Int = 30,
    )
}