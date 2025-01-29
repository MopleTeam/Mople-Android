package com.moim.core.data.datasource.auth

import com.moim.core.data.datasource.auth.remote.AuthRemoteDataSource
import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.datastore.PreferenceStorage
import com.moim.core.model.Token
import com.moim.core.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource,
    private val preferenceStorage: PreferenceStorage,
) : AuthRepository {

    override fun getToken(): Flow<Token?> {
        return preferenceStorage.token.map { it?.asItem() }
    }

    override fun signUp(socialType: String, token: String, email: String, nickname: String, profileUrl: String?) = flow {
        val uploadProfileUrl = imageUploadRemoteDataSource.uploadImage(url = profileUrl, "profile")
        emit(authRemoteDataSource.signUp(socialType, token, email, nickname, uploadProfileUrl).also { preferenceStorage.saveUserToken(it) }.asItem())
    }

    override fun signIn(socialType: String, token: String, email: String) = flow {
        emit(authRemoteDataSource.signIn(socialType, token, email).also { preferenceStorage.saveUserToken(it) }.asItem())
    }
}