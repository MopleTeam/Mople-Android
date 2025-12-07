package com.moim.feature.intro.screen.splash

import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.policy.PolicyRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val policyRepository: PolicyRepository,
) : BaseViewModel() {

    private val splashResult =
        authRepository
            .getToken()
            .flatMapLatest { token ->
                combine(
                    if (token != null) userRepository.fetchUser() else flowOf(null),
                    policyRepository.getForceUpdateInfo(),
                    ::Pair
                )
            }.asResult()

    init {
        setUiState(SplashUiState.Splash())
        validateUser()
    }

    fun onUiAction(uiAction: SplashUiAction) {
        when (uiAction) {
            is SplashUiAction.OnClickExit -> setUiEvent(SplashUiEvent.NavigateToExit)
            is SplashUiAction.OnClickForceUpdate -> setUiEvent(SplashUiEvent.NavigateToPlayStore)
        }
    }

    private fun validateUser() {
        viewModelScope.launch {
            splashResult.collect { result ->
                when (result) {
                    is Result.Loading -> return@collect
                    is Result.Success -> {
                        val (user, forceUpdateState) = result.data

                        when {
                            forceUpdateState.isForceUpdate -> {
                                setUiState(SplashUiState.Splash(isShowForceUpdateDialog = true))
                            }

                            user == null -> {
                                delay(500)
                                setUiEvent(SplashUiEvent.NavigateToSignIn)
                            }

                            else -> {
                                setUiEvent(SplashUiEvent.NavigateToMain)
                            }
                        }
                    }

                    is Result.Error -> when (result.exception) {
                        is IOException -> setUiState(SplashUiState.Splash(isShowErrorDialog = true))
                        is NetworkException -> setUiEvent(SplashUiEvent.NavigateToSignIn).also { userRepository.clearMoimStorage() }
                    }
                }

            }
        }
    }
}

sealed interface SplashUiState : UiState {
    data class Splash(
        val isShowErrorDialog: Boolean = false,
        val isShowForceUpdateDialog: Boolean = false
    ) : SplashUiState
}

sealed interface SplashUiAction : UiAction {
    data object OnClickExit : SplashUiAction

    data object OnClickForceUpdate : SplashUiAction
}

sealed interface SplashUiEvent : UiEvent {
    data object NavigateToSignIn : SplashUiEvent

    data object NavigateToMain : SplashUiEvent

    data object NavigateToExit : SplashUiEvent

    data object NavigateToPlayStore : SplashUiEvent
}