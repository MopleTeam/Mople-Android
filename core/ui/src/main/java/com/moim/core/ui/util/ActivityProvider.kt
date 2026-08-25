package com.moim.core.ui.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

// Activity Context가 필요한 SDK 호출을 ViewModel에서 하기 위한 현재 Activity 보관소
@Singleton
class ActivityProvider @Inject constructor() : Application.ActivityLifecycleCallbacks {
    private var activityRef: WeakReference<Activity>? = null

    val currentActivity: Activity?
        get() = activityRef?.get()?.takeIf { it.isFinishing.not() && it.isDestroyed.not() }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?,
    ) = Unit

    override fun onActivityStarted(activity: Activity) = Unit

    override fun onActivityResumed(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle,
    ) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (activityRef?.get() === activity) activityRef = null
    }
}
