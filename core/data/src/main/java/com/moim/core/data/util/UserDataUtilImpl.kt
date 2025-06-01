package com.moim.core.data.util

import com.moim.core.datamodel.TokenResponse
import com.moim.core.datastore.PreferenceStorage
import com.moim.core.network.util.UserDataUtil
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataUtilImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : UserDataUtil {

    override val token: Flow<TokenResponse?> = preferenceStorage.token

    override suspend fun saveUserToken(token: TokenResponse) {
        preferenceStorage.saveUserToken(token)
    }
}