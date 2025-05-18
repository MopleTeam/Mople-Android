package com.moim.core.data.datasource.token

import com.moim.core.data.service.TokenApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.catchFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    private val tokenApi: TokenApi,
) : TokenRepository {

    override fun setFcmToken(token: String): Flow<Unit> = catchFlow {
        emit(
            tokenApi.setFcmToken(
                jsonOf(
                    KEY_TOKEN to token,
                    KEY_SUBSCRIBE to true
                )
            )
        )
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_SUBSCRIBE = "subscribe"
    }
}