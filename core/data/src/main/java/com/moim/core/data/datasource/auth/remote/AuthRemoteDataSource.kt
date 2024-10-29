package com.moim.core.data.datasource.auth.remote

import com.moim.core.data.model.TokenResponse

internal interface AuthRemoteDataSource {

    suspend fun signUp(socialType: String, token: String) : TokenResponse

    suspend fun signIn(socialType: String, token: String): TokenResponse
}