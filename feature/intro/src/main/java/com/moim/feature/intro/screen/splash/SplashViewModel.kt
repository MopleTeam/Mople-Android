package com.moim.feature.intro.screen.splash

import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.model.asItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : BaseViewModel() {

    init {
        setUiState(SplashState.Splash())
        validateUser()
    }

    private fun validateUser() {
        viewModelScope.launch {
            val token = authRepository.getToken().first()?.asItem()

            if (token == null) {
                delay(700)
                setUiEvent(SplashEvent.NavigateToSignIn)
            } else {
                authRepository
                    .signIn(socialType = "kakao", token.accessToken)
                    .asResult()
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> {}
                            is Result.Error -> {}
                        }
                    }
            }
        }
    }
}

sealed interface SplashState : UiState {
    data class Splash(
        val isShowErrorDialog: Boolean = false
    ) : SplashState
}

sealed interface SplashEvent : UiEvent {
    data object NavigateToSignIn : SplashEvent
    data object NavigateToMain : SplashEvent
}