package com.moim.core.remote.model

import com.moim.core.common.model.Token
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("accessToken")
    val accessToken: String = "",
    @SerialName("refreshToken")
    val refreshToken: String ="",
)

fun TokenResponse.asItem(): Token {
    return Token(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}