package com.moim.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moim.core.data.datastore.PreferenceStorage.PreferenceKeys.PREF_USER
import com.moim.core.data.datastore.PreferenceStorage.PreferenceKeys.PREF_USER_TOKEN
import com.moim.core.data.model.TokenResponse
import com.moim.core.data.model.UserResponse
import com.moim.core.data.util.JsonUtil.toJson
import com.moim.core.data.util.JsonUtil.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

internal class PreferenceStorage @Inject constructor(
    private val preference: DataStore<Preferences>,
) {

    object PreferenceKeys {
        val PREF_USER = stringPreferencesKey("pref_user")
        val PREF_USER_TOKEN = stringPreferencesKey("pref_user_token")
    }

    // ==================== User ========================== //
    val user : Flow<UserResponse?> = preference.data.map {
        try {
            it[PREF_USER]?.toObject<UserResponse>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUser(user: UserResponse) {
        preference.edit {
            it[PREF_USER] = try {
                user.toJson()
            } catch (e: Exception) {
                Timber.e("[saveUser Exception]:$e")
                ""
            }
        }
    }

    // ==================== Token ========================== //
    val token: Flow<TokenResponse?> = preference.data.map {
        try {
            it[PREF_USER_TOKEN]?.toObject<TokenResponse>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserToken(token: TokenResponse) {
        preference.edit {
            it[PREF_USER_TOKEN] = try {
                token.toJson()
            } catch (e: Exception) {
                Timber.e("[saveUserToken Exception]:$e")
                ""
            }
        }
    }

    suspend fun clearMoimStorage() {
        preference.edit { it.clear() }
    }

    companion object {
        const val PREFS_MOIM = "prefsMoim"
    }
}