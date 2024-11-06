package com.moim.core.model

import com.moim.core.data.model.TokenResponse

data class Token(
    val accessToken: String,
    val refreshToken: String,
)

fun TokenResponse.asItem(): Token {
    return Token(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}

fun Token.asInternalModel(): TokenResponse {
    return TokenResponse(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}