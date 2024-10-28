package com.moim.feature.intro.screen.splash

import androidx.lifecycle.SavedStateHandle
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

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