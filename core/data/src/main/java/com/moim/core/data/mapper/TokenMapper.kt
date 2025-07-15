package com.moim.core.data.mapper

import com.moim.core.datamodel.TokenResponse
import com.moim.core.model.Token


fun TokenResponse.asItem(): Token {
    return Token(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}