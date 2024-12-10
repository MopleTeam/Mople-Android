package com.moim.core.data.datasource.token.remote

internal interface TokenRemoteDataSource {

    suspend fun setFcmToken(token : String)
}