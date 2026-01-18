package com.moim.feature.themesetting

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Theme
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeSettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : BaseViewModel() {
    private val theme = userRepository.getTheme()

    init {
        viewModelScope.launch {
            theme.collect { theme ->
                setUiState(ThemeSettingUiState(theme))
            }
        }
    }

    fun onUiAction(uiAction: ThemeSettingUiAction) {
        when (uiAction) {
            is ThemeSettingUiAction.OnClickBack -> setUiEvent(ThemeSettingUiEvent.NavigateToBack)
            is ThemeSettingUiAction.OnClickTheme -> setUiTheme(uiAction.theme)
        }
    }

    private fun setUiTheme(theme: Theme) {
        viewModelScope.launch {
            userRepository.setTheme(theme)
        }
    }
}

data class ThemeSettingUiState(
    val theme: Theme = Theme.SYSTEM,
) : UiState

sealed interface ThemeSettingUiAction : UiAction {
    data class OnClickTheme(
        val theme: Theme,
    ) : ThemeSettingUiAction

    data object OnClickBack : ThemeSettingUiAction
}

sealed interface ThemeSettingUiEvent : UiEvent {
    data object NavigateToBack : ThemeSettingUiEvent
}
