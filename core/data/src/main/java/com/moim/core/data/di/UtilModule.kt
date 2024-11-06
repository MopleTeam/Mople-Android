package com.moim.core.data.di

import android.content.Context
import com.moim.core.data.util.CompressorUtil
import com.moim.core.data.util.FileUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object UtilModule {

    @Singleton
    @Provides
    fun provideCompressorUtil(
        @ApplicationContext context: Context
    ): CompressorUtil = CompressorUtil(context)

    @Singleton
    @Provides
    fun provideFileUtil(
        @ApplicationContext context: Context
    ): FileUtil = FileUtil(context)
}