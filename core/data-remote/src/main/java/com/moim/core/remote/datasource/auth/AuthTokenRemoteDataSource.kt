package com.moim.core.remote.datasource.auth

import com.moim.core.remote.model.TokenResponse

interface AuthTokenRemoteDataSource {
    suspend fun getRefreshToken(refreshToken: String?): TokenResponse
}
