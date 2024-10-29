package com.moim.core.data.datasource.auth

import com.moim.core.data.model.TokenResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun getToken() : Flow<TokenResponse?>

    fun signUp(socialType: String, token: String): Flow<TokenResponse>

    fun signIn(socialType: String, token: String): Flow<TokenResponse>
}