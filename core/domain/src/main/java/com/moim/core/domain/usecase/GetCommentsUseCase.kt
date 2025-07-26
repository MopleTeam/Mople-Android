package com.moim.core.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.model.Comment
import timber.log.Timber
import java.time.ZonedDateTime
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
) {
    var loadedAt: ZonedDateTime = ZonedDateTime.now()

    operator fun invoke(params: Params) = Pager(
        config = PagingConfig(pageSize = params.size)
    ) {
        object : PagingSource<String, Comment>() {

            init {
                loadedAt = ZonedDateTime.now()
            }

            override fun getRefreshKey(state: PagingState<String, Comment>): String? = null

            override suspend fun load(loadParams: LoadParams<String>): LoadResult<String, Comment> {
                val page = loadParams.key ?: ""

                return try {
                    val commentContainer = commentRepository.getComments(
                        postId = params.postId,
                        cursor = page,
                        size = params.size
                    )
                    val nextCursor = commentContainer.cursorPage.nextCursor

                    LoadResult.Page(
                        data = commentContainer.content,
                        prevKey = null,
                        nextKey = if (commentContainer.cursorPage.isNext && commentContainer.cursorPage.size >= params.size) nextCursor else null
                    )
                } catch (e: Exception) {
                    Timber.e("[GetCommentsUseCase] error $e")
                    LoadResult.Error(e)
                }
            }
        }
    }.flow

    data class Params(
        val postId: String,
        val size: Int = 30,
    )
}