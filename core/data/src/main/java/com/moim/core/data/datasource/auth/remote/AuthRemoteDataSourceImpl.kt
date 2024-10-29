package com.moim.core.data.datasource.auth.remote

import com.moim.core.data.model.TokenResponse
import com.moim.core.data.service.AuthApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.converterException
import javax.inject.Inject

internal class AuthRemoteDataSourceImpl @Inject constructor(
    private val authApi: AuthApi,
) : AuthRemoteDataSource {

    override suspend fun signUp(socialType: String, token: String): TokenResponse {
        return try {
            authApi.signUp(
                params = jsonOf(
                    KEY_SOCIAL_PROVIDER to socialType,
                    KEY_PROVIDER_TOKEN to token,
                    KEY_DEVICE_TYPE to "ANDROID"
                )
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun signIn(socialType: String, token: String): TokenResponse {
        return try {
            authApi.signIn(
                params = jsonOf(
                    KEY_SOCIAL_PROVIDER to socialType,
                    KEY_PROVIDER_TOKEN to token,
                )
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    companion object {
        private const val KEY_SOCIAL_PROVIDER = "socialProvider"
        private const val KEY_PROVIDER_TOKEN = "providerToken"
        private const val KEY_DEVICE_TYPE = "deviceType"
    }
}