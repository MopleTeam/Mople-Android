package com.moim.core.remote.util

import com.moim.core.common.model.Token
import kotlinx.coroutines.flow.Flow

interface UserDataUtil {
    val token: Flow<Token?>

    suspend fun saveUserToken(token: Token)
}
