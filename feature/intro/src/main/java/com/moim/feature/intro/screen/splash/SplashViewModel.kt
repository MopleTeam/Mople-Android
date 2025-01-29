package com.moim.feature.intro.screen.splash

import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : BaseViewModel() {

    init {
        setUiState(SplashUiState.Splash())
        validateUser()
    }

    private fun validateUser() {
        viewModelScope.launch {
            val token = authRepository.getToken().first()

            if (token == null) {
                delay(500)
                setUiEvent(SplashUiEvent.NavigateToSignIn)
            } else {
                userRepository.fetchUser()
                    .asResult()
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiEvent(SplashUiEvent.NavigateToMain)
                            is Result.Error -> when (result.exception) {
                                is IOException -> setUiState(SplashUiState.Splash(isShowErrorDialog = true))
                                is NetworkException -> setUiEvent(SplashUiEvent.NavigateToSignIn).also { userRepository.clearMoimStorage() }
                            }
                        }
                    }
            }
        }
    }
}

sealed interface SplashUiState : UiState {
    data class Splash(
        val isShowErrorDialog: Boolean = false
    ) : SplashUiState
}

sealed interface SplashUiEvent : UiEvent {
    data object NavigateToSignIn : SplashUiEvent
    data object NavigateToMain : SplashUiEvent
}