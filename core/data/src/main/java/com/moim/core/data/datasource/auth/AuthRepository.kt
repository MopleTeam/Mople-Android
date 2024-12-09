package com.moim.core.data.datasource.auth

import com.moim.core.datamodel.TokenResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun getToken(): Flow<TokenResponse?>

    fun signUp(socialType: String, token: String, email: String, nickname: String, profileUrl: String? = null): Flow<TokenResponse>

    fun signIn(socialType: String, token: String, email: String,): Flow<TokenResponse>
}