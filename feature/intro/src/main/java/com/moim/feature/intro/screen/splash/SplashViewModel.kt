package com.moim.feature.intro.screen.splash

import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.NetworkException
import com.moim.core.crashreport.CrashReporter
import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.policy.PolicyRepository
import com.moim.core.data.datasource.token.TokenRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.feature.intro.screen.splash.model.SplashIntent
import com.moim.feature.intro.screen.splash.model.SplashSideEffect
import com.moim.feature.intro.screen.splash.model.SplashState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val policyRepository: PolicyRepository,
    private val tokenRepository: TokenRepository,
    private val crashReporter: CrashReporter,
) : MVIViewModel<SplashState, SplashSideEffect>(SplashState()) {
    override suspend fun Syntax<SplashState, SplashSideEffect>.onContainerCreate() {
        validateUser()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is SplashIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is SplashIntent.ExitClick -> {
                    postSideEffect(SplashSideEffect.NavigateToExit)
                }

                is SplashIntent.ForceUpdateClick -> {
                    postSideEffect(SplashSideEffect.NavigateToPlayStore)
                }
            }
        }
    }

    private suspend fun Syntax<SplashState, SplashSideEffect>.validateUser() {
        try {
            val token = authRepository.getToken().first()
            val (user, forceUpdateInfo) =
                coroutineScope {
                    val userAsync = async { if (token != null) userRepository.fetchUser() else null }
                    val forceUpdateInfoAsync = async { policyRepository.getForceUpdateInfo() }

                    userAsync.await() to forceUpdateInfoAsync.await()
                }

            when {
                forceUpdateInfo.isForceUpdate -> {
                    reduce { state.copy(isShowForceUpdateDialog = true) }
                }

                user == null -> {
                    delay(timeMillis = 500)
                    postSideEffect(SplashSideEffect.NavigateToSignIn)
                }

                else -> {
                    syncFcmToken()
                    postSideEffect(SplashSideEffect.NavigateToMain)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: NetworkException) {
            postSideEffect(SplashSideEffect.NavigateToSignIn)
            userRepository.clearMoimStorage()
        } catch (e: Exception) {
            if (e !is IOException) crashReporter.logException(e)
            reduce { state.copy(isShowErrorDialog = true) }
        }
    }

    // 메인 진입을 막지 않도록 별도 코루틴에서 처리
    private fun syncFcmToken() {
        viewModelScope.launch {
            try {
                tokenRepository.syncFcmTokenIfNeeded()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                crashReporter.logException(e)
            }
        }
    }
}
