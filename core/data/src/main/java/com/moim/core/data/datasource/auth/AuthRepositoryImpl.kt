package com.moim.core.data.datasource.auth

import com.moim.core.data.datasource.auth.remote.AuthRemoteDataSource
import com.moim.core.data.datastore.PreferenceStorage
import com.moim.core.data.model.TokenResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val preferenceStorage: PreferenceStorage,
) : AuthRepository {

    override fun getToken(): Flow<TokenResponse?> {
        return preferenceStorage.token
    }

    override fun signUp(socialType: String, token: String) = flow {
        emit(authRemoteDataSource.signUp(socialType, token).also { preferenceStorage.saveUserToken(it) })
    }

    override fun signIn(socialType: String, token: String) = flow {
        emit(authRemoteDataSource.signIn(socialType, token).also { preferenceStorage.saveUserToken(it) })
    }
}