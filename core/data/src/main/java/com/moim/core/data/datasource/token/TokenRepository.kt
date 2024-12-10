package com.moim.core.data.datasource.token

import kotlinx.coroutines.flow.Flow

interface TokenRepository {

    fun setFcmToken(token: String) : Flow<Unit>
}