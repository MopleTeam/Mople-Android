package com.moim.core.crashreport

interface CrashReporter {
    fun setUserId(userId: String)

    fun log(message: String)

    fun logException(throwable: Throwable)
}
