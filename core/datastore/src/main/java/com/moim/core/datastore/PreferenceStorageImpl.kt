package com.moim.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moim.core.common.util.JsonUtil.toJson
import com.moim.core.common.util.JsonUtil.toObject
import com.moim.core.datamodel.TokenResponse
import com.moim.core.datamodel.UserResponse
import com.moim.core.datastore.PreferenceStorageImpl.PreferenceKeys.PREF_USER
import com.moim.core.datastore.PreferenceStorageImpl.PreferenceKeys.PREF_USER_TOKEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

internal class PreferenceStorageImpl @Inject constructor(
    private val preference: DataStore<Preferences>,
) : PreferenceStorage {

    object PreferenceKeys {
        val PREF_USER = stringPreferencesKey("pref_user")
        val PREF_USER_TOKEN = stringPreferencesKey("pref_user_token")
    }

    override val user: Flow<UserResponse?> = preference.data.map {
        try {
            it[PREF_USER]?.toObject<UserResponse>()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveUser(user: UserResponse) {
        preference.edit {
            it[PREF_USER] = try {
                user.toJson()
            } catch (e: Exception) {
                Timber.e("[saveUser Exception]:$e")
                ""
            }
        }
    }

    override val token: Flow<TokenResponse?> = preference.data.map {
        try {
            it[PREF_USER_TOKEN]?.toObject<TokenResponse>()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveUserToken(token: TokenResponse) {
        preference.edit {
            it[PREF_USER_TOKEN] = try {
                token.toJson()
            } catch (e: Exception) {
                Timber.e("[saveUserToken Exception]:$e")
                ""
            }
        }
    }

    override suspend fun clearMoimStorage() {
        preference.edit { it.clear() }
    }

    companion object {
        const val PREFS_MOIM = "prefsMoim"
    }
}