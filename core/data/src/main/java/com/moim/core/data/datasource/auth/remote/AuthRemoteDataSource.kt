package com.moim.core.data.datasource.auth.remote

import com.moim.core.datamodel.TokenResponse

internal interface AuthRemoteDataSource {

    suspend fun signUp(socialType: String, token: String, email: String, nickname: String, profileUrl: String? = null): TokenResponse

    suspend fun signIn(socialType: String, token: String, email: String): TokenResponse
}