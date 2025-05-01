package com.moim.core.analytics

import androidx.compose.runtime.staticCompositionLocalOf

class NoOpAnalyticsHelper : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) = Unit
}

val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    NoOpAnalyticsHelper()
}