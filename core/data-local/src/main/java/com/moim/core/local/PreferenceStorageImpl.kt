package com.moim.core.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moim.core.common.model.Token
import com.moim.core.common.model.User
import com.moim.core.common.util.JsonUtil.toJson
import com.moim.core.common.util.JsonUtil.toObject
import com.moim.core.local.PreferenceStorageImpl.PreferenceKeys.PREF_USER
import com.moim.core.local.PreferenceStorageImpl.PreferenceKeys.PREF_USER_TOKEN
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

    override val user: Flow<User?> =
        preference.data.map {
            try {
                it[PREF_USER]?.toObject<User>()
            } catch (e: Exception) {
                null
            }
        }

    override suspend fun saveUser(user: User) {
        preference.edit {
            it[PREF_USER] =
                try {
                    user.toJson()
                } catch (e: Exception) {
                    Timber.e("[saveUser Exception]:$e")
                    ""
                }
        }
    }

    override val token: Flow<Token?> =
        preference.data.map {
            try {
                it[PREF_USER_TOKEN]?.toObject<Token>()
            } catch (e: Exception) {
                null
            }
        }

    override suspend fun saveUserToken(token: Token) {
        preference.edit {
            it[PREF_USER_TOKEN] =
                try {
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
