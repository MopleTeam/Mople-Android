package com.moim.feature.themesetting

import androidx.lifecycle.viewModelScope
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.feature.themesetting.model.ThemeSettingIntent
import com.moim.feature.themesetting.model.ThemeSettingSideEffect
import com.moim.feature.themesetting.model.ThemeSettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ThemeSettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : MVIViewModel<ThemeSettingState, ThemeSettingSideEffect>(ThemeSettingState()) {
    init {
        userRepository
            .getTheme()
            .onEach { theme -> intent { reduce { state.copy(theme = theme) } } }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: Intent) {
        if (intent !is ThemeSettingIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is ThemeSettingIntent.BackClick -> {
                    postSideEffect(ThemeSettingSideEffect.NavigateToBack)
                }

                is ThemeSettingIntent.ThemeClick -> {
                    userRepository.setTheme(intent.theme)
                }
            }
        }
    }
}
