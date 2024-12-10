package com.moim.core.data.datasource.token

import com.moim.core.data.datasource.token.remote.TokenRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    private val remoteDataSource: TokenRemoteDataSource
) : TokenRepository {

    override fun setFcmToken(token: String): Flow<Unit> = flow {
        emit(remoteDataSource.setFcmToken(token))
    }
}