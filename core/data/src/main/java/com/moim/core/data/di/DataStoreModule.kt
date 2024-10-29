package com.moim.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.moim.core.data.datastore.PreferenceStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {

    private val Context.preference: DataStore<Preferences> by preferencesDataStore(
        name = PreferenceStorage.PREFS_MOIM
    )

    @Singleton
    @Provides
    fun providePreferenceStorageModule(
        @ApplicationContext context: Context,
    ): PreferenceStorage {
        return PreferenceStorage(
            preference = context.preference,
        )
    }
}