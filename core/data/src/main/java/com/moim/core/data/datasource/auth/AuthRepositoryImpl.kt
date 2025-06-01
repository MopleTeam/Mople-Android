package com.moim.core.data.datasource.auth

import com.moim.core.common.consts.DEVICE_TYPE_ANDROID
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.util.catchFlow
import com.moim.core.datastore.PreferenceStorage
import com.moim.core.model.Token
import com.moim.core.model.asItem
import com.moim.core.network.service.AuthApi
import com.moim.core.network.util.convertToToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource,
    private val preferenceStorage: PreferenceStorage,
) : AuthRepository {

    override fun getToken(): Flow<Token?> {
        return preferenceStorage.token.map { it?.asItem() }
    }

    override fun signUp(socialType: String, token: String, email: String, nickname: String, profileUrl: String?) = catchFlow {
        val uploadProfileUrl = imageUploadRemoteDataSource.uploadImage(profileUrl, "profile")
        val authToken = authApi.signUp(
            params = jsonOf(
                KEY_SOCIAL_PROVIDER to socialType,
                KEY_PROVIDER_TOKEN to token,
                KEY_EMAIL to email,
                KEY_NICKNAME to nickname,
                KEY_IMAGE to uploadProfileUrl,
                KEY_DEVICE_TYPE to DEVICE_TYPE_ANDROID
            )
        ).also { preferenceStorage.saveUserToken(it) }.asItem()

        emit(authToken)
    }

    override fun signIn(socialType: String, token: String, email: String) = catchFlow {
        val authToken = authApi.signIn(
            params = jsonOf(
                KEY_SOCIAL_PROVIDER to socialType,
                KEY_PROVIDER_TOKEN to token,
                KEY_EMAIL to email
            )
        ).also { preferenceStorage.saveUserToken(it) }.asItem()

        emit(authToken)
    }

    override fun signOut(userId: String): Flow<Unit> = catchFlow {
        val token = preferenceStorage.token.first()?.accessToken

        emit(
            authApi.signOut(
                token = token.convertToToken(),
                params = jsonOf(
                    KEY_ID to userId,
                    KEY_ROLE to "ADMIN"
                )
            )
        )
    }

    companion object {
        private const val KEY_SOCIAL_PROVIDER = "socialProvider"
        private const val KEY_PROVIDER_TOKEN = "providerToken"
        private const val KEY_EMAIL = "email"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_IMAGE = "image"
        private const val KEY_ID = "id"
        private const val KEY_ROLE = "role"
        private const val KEY_DEVICE_TYPE = "deviceType"
    }
}