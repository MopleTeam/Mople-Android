package com.moim.core.data.datasource.auth

import com.moim.core.common.model.Token
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getToken(): Flow<Token?>

    fun signUp(
        socialType: String,
        token: String,
        email: String,
        nickname: String,
        profileUrl: String? = null,
    ): Flow<Token>

    fun signIn(
        socialType: String,
        token: String,
        email: String,
    ): Flow<Token>

    fun signOut(userId: String): Flow<Unit>
}
