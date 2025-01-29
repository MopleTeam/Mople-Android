package com.moim.feature.profile

import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val userResult = loadDataSignal
        .flatMapLatest { userRepository.getUser().asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            userResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(ProfileUiState.Loading)
                    is Result.Success -> setUiState(ProfileUiState.Success(result.data))
                    is Result.Error -> setUiState(ProfileUiState.Error)
                }
            }
        }
    }

    fun onUiAction(uiAction: ProfileUiAction) {
        when (uiAction) {
            is ProfileUiAction.OnClickProfile -> setUiEvent(ProfileUiEvent.NavigateToProfileUpdate)
            is ProfileUiAction.OnClickAlarmSetting -> setUiEvent(ProfileUiEvent.NavigateToAlarmSetting)
            is ProfileUiAction.OnClickPrivacyPolicy -> setUiEvent(ProfileUiEvent.NavigateToPrivacyPolicy)
            is ProfileUiAction.OnClickLogout -> logout()
            is ProfileUiAction.OnClickUserDelete -> deleteUser()
            is ProfileUiAction.OnClickRefresh -> onRefresh()
            is ProfileUiAction.OnShowUserLogoutDialog -> showUserLogoutDialog(uiAction.isShow)
            is ProfileUiAction.OnShowUserDeleteDialog -> showUserDeleteDialog(uiAction.isShow)
        }
    }

    private fun showUserLogoutDialog(isShow: Boolean) {
        uiState.checkState<ProfileUiState.Success> {
            setUiState(copy(isShowUserLogoutDialog = isShow))
        }
    }

    private fun showUserDeleteDialog(isShow: Boolean) {
        uiState.checkState<ProfileUiState.Success> {
            setUiState(copy(isShowUserDeleteDialog = isShow))
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.clearMoimStorage()
            setUiEvent(ProfileUiEvent.NavigateToIntro)
        }
    }

    private fun deleteUser() {
        viewModelScope.launch {
            uiState.checkState<ProfileUiState.Success> {
                userRepository.deleteUser()
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when(result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> logout()
                            is Result.Error -> when(result.exception) {
                                is IOException -> setUiEvent(ProfileUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                else -> setUiEvent(ProfileUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                            }
                        }
                    }
            }
        }
    }
}

sealed interface ProfileUiState : UiState {
    data object Loading : ProfileUiState

    data class Success(
        val user: User,
        val isShowUserLogoutDialog: Boolean = false,
        val isShowUserDeleteDialog: Boolean = false
    ) : ProfileUiState

    data object Error : ProfileUiState
}

sealed interface ProfileUiAction : UiAction {
    data object OnClickProfile : ProfileUiAction
    data object OnClickAlarmSetting : ProfileUiAction
    data object OnClickPrivacyPolicy : ProfileUiAction
    data object OnClickLogout : ProfileUiAction
    data object OnClickUserDelete : ProfileUiAction
    data object OnClickRefresh : ProfileUiAction
    data class OnShowUserLogoutDialog(val isShow: Boolean) : ProfileUiAction
    data class OnShowUserDeleteDialog(val isShow: Boolean) : ProfileUiAction
}

sealed interface ProfileUiEvent : UiEvent {
    data object NavigateToProfileUpdate : ProfileUiEvent
    data object NavigateToAlarmSetting : ProfileUiEvent
    data object NavigateToPrivacyPolicy : ProfileUiEvent
    data object NavigateToIntro : ProfileUiEvent
    data class ShowToastMessage(val message: ToastMessage) : ProfileUiEvent
}