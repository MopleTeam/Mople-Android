package com.moim.core.data.datasource.token.remote

import com.moim.core.data.service.TokenApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.converterException
import javax.inject.Inject

internal class TokenRemoteDataSourceImpl @Inject constructor(
    private val tokenApi: TokenApi
) : TokenRemoteDataSource {

    override suspend fun setFcmToken(token: String) {
        return try {
            tokenApi.setFcmToken(jsonOf(KEY_TOKEN to token))
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    companion object {
        private const val KEY_TOKEN = "token"
    }
}