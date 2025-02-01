package com.moim.core.data.datasource.comment

import com.moim.core.data.datasource.comment.remote.CommentRemoteDataSource
import com.moim.core.datamodel.CommentResponse
import com.moim.core.model.Comment
import com.moim.core.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class CommentRepositoryImpl @Inject constructor(
    private val remoteDataSource: CommentRemoteDataSource
) : CommentRepository {

    override fun getComments(postId: String): Flow<List<Comment>> = flow {
        emit(remoteDataSource.getComments(postId).map(CommentResponse::asItem))
    }

    override fun createComment(postId: String, content: String): Flow<List<Comment>> = flow {
        emit(remoteDataSource.createComment(postId, content).map(CommentResponse::asItem))
    }

    override fun updateComment(commentId: String, content: String): Flow<List<Comment>> = flow {
        emit(remoteDataSource.updateComment(commentId, content).map(CommentResponse::asItem))
    }

    override fun deleteComment(commentId: String): Flow<Unit> = flow {
        emit(remoteDataSource.deleteComment(commentId))
    }

    override fun reportComment(commentId: String): Flow<Unit> = flow {
        emit(remoteDataSource.reportComment(commentId))
    }
}