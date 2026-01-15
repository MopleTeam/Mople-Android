package com.moim.core.crashreport

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashReporterImpl @Inject constructor(
    private val crashlytics: FirebaseCrashlytics,
) : CrashReporter {
    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun logException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}
