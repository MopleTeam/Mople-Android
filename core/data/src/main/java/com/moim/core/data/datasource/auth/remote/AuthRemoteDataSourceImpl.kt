package com.moim.core.data.datasource.auth.remote

import com.moim.core.common.consts.DEVICE_TYPE_ANDROID
import com.moim.core.datamodel.TokenResponse
import com.moim.core.data.service.AuthApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.converterException
import javax.inject.Inject

internal class AuthRemoteDataSourceImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRemoteDataSource {

    override suspend fun signUp(
        socialType: String,
        token: String,
        email: String,
        nickname: String,
        profileUrl: String?
    ): TokenResponse {
        return try {
            authApi.signUp(
                params = jsonOf(
                    KEY_SOCIAL_PROVIDER to socialType,
                    KEY_PROVIDER_TOKEN to token,
                    KEY_EMAIL to email,
                    KEY_NICKNAME to nickname,
                    KEY_IMAGE to profileUrl,
                    KEY_DEVICE_TYPE to DEVICE_TYPE_ANDROID
                )
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun signIn(
        socialType: String,
        token: String,
        email: String
    ): TokenResponse {
        return try {
            authApi.signIn(
                params = jsonOf(
                    KEY_SOCIAL_PROVIDER to socialType,
                    KEY_PROVIDER_TOKEN to token,
                    KEY_EMAIL to email
                )
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    companion object {
        private const val KEY_SOCIAL_PROVIDER = "socialProvider"
        private const val KEY_PROVIDER_TOKEN = "providerToken"
        private const val KEY_EMAIL = "email"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_IMAGE = "image"
        private const val KEY_DEVICE_TYPE = "deviceType"
    }
}