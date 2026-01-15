package com.moim.core.crashreport.di

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moim.core.crashreport.CrashReporter
import com.moim.core.crashreport.CrashReporterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CrashReportingModule {
    @Singleton
    @Binds
    abstract fun bindsCrashReporter(crashReporterImpl: CrashReporterImpl): CrashReporter

    companion object {
        @Provides
        @Singleton
        fun providesFirebaseCrashlytics(
            @ApplicationContext
            context: Context,
        ) = FirebaseCrashlytics.getInstance()
    }
}
