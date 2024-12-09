package com.moim.core.model

import com.moim.core.datamodel.TokenResponse

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