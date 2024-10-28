package com.moim.core.data.datasource.auth

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun signUp(socialType: String, token: String): Flow<Unit>

    fun signIn(socialType: String, token: String): Flow<Unit>
}