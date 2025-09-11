package com.moim.core.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.moim.core.common.di.IoDispatcher
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.model.Comment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.time.ZonedDateTime
import javax.inject.Inject

class GetReplyCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
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
                    val commentContainer = commentRepository.getReplyComments(
                        postId = params.postId,
                        commentId = params.commentId,
                        cursor = page,
                        size = params.size
                    )
                    val nextCursor = commentContainer.page.nextCursor

                    LoadResult.Page(
                        data = commentContainer.content,
                        prevKey = null,
                        nextKey = if (commentContainer.page.isNext && commentContainer.page.size >= params.size) nextCursor else null
                    )
                } catch (e: Exception) {
                    Timber.e("[GetReplyCommentUseCase] error $e")
                    LoadResult.Error(e)
                }
            }
        }
    }.flow.flowOn(ioDispatcher)

    data class Params(
        val postId: String,
        val commentId: String,
        val size: Int = 30,
    )
}