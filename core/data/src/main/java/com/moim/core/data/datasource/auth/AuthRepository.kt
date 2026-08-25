package com.moim.core.data.datasource.auth

import com.moim.core.common.model.Token
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getToken(): Flow<Token?>

    suspend fun signUp(
        socialType: String,
        token: String,
        email: String,
        nickname: String,
        profileUrl: String? = null,
    ): Token

    suspend fun signIn(
        socialType: String,
        token: String,
        email: String,
    ): Token

    suspend fun signOut(userId: String)
}
