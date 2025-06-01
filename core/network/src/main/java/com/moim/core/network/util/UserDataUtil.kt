package com.moim.core.network.util

import com.moim.core.datamodel.TokenResponse
import kotlinx.coroutines.flow.Flow

interface UserDataUtil {
    val token: Flow<TokenResponse?>

    suspend fun saveUserToken(token: TokenResponse)
}