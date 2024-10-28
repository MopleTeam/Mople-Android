package com.moim.core.data.datasource.auth

import com.moim.core.data.datasource.auth.remote.AuthRemoteDataSource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override fun signUp(socialType: String, token: String) = flow {
        authRemoteDataSource.signUp(socialType, token)
        emit(Unit)
    }

    override fun signIn(socialType: String, token: String) = flow {
        authRemoteDataSource.signIn(socialType, token)
        emit(Unit)
    }
}