package com.moim.feature.intro.kakao

import android.app.Activity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.moim.core.ui.util.ActivityProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class KakaoAccount(
    val token: String,
    val email: String,
)

// 카카오 로그인은 Activity Context를 요구하므로 ActivityProvider를 통해 주입받는다
@Singleton
class KakaoLoginClient @Inject constructor(
    private val activityProvider: ActivityProvider,
) {
    suspend fun login(): KakaoAccount {
        val activity = requireNotNull(activityProvider.currentActivity) { "activity is not attached" }
        val token = requestToken(activity)

        return KakaoAccount(token = token.idToken.toString(), email = requestEmail())
    }

    private suspend fun requestToken(activity: Activity): OAuthToken =
        suspendCancellableCoroutine { continuation ->
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, exception ->
                when {
                    exception != null -> continuation.resumeWithException(exception)
                    token != null -> continuation.resume(token)
                    else -> continuation.resumeWithException(IllegalStateException("kakao token is null"))
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(activity)) {
                UserApiClient.instance.loginWithKakaoTalk(context = activity, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context = activity, callback = callback)
            }
        }

    private suspend fun requestEmail(): String =
        suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.me { user, exception ->
                if (user != null) {
                    continuation.resume(user.kakaoAccount?.email.toString())
                } else {
                    continuation.resumeWithException(exception ?: IllegalStateException("kakao user is null"))
                }
            }
        }
}
