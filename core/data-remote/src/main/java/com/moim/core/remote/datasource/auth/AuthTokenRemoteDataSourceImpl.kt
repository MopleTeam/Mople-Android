package com.moim.core.remote.datasource.auth

import com.moim.core.remote.di.qualifiers.TokenRefreshApi
import com.moim.core.remote.model.TokenResponse
import com.moim.core.remote.util.HEADER_REFRESH
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import javax.inject.Inject

internal class AuthTokenRemoteDataSourceImpl @Inject constructor(
    @TokenRefreshApi private val client: HttpClient,
) : AuthTokenRemoteDataSource {
    override suspend fun getRefreshToken(refreshToken: String?): TokenResponse =
        client
            .post("auth/recreate") {
                header(HEADER_REFRESH, refreshToken)
            }.body()
}
