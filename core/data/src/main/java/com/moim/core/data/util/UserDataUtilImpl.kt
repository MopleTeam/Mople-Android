package com.moim.core.data.util

import com.moim.core.common.model.Token
import com.moim.core.local.PreferenceStorage
import com.moim.core.remote.util.UserDataUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataUtilImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
) : UserDataUtil {
    override val token: Flow<Token?> = preferenceStorage.token

    override suspend fun saveUserToken(token: Token) {
        preferenceStorage.saveUserToken(token)
    }
}
