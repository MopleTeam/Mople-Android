package com.moim.feature.profile

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.User
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.core.ui.view.restartableStateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val meetingRepository: MeetingRepository,
) : BaseViewModel() {
    private val userResult =
        userRepository
            .getUser()
            .asResult()
            .restartableStateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

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
            is ProfileUiAction.OnClickThemeSetting -> setUiEvent(ProfileUiEvent.NavigateToThemeSetting)
            is ProfileUiAction.OnClickPrivacyPolicy -> setUiEvent(ProfileUiEvent.NavigateToPrivacyPolicy)
            is ProfileUiAction.OnClickLogout -> logout()
            is ProfileUiAction.OnClickUserDelete -> deleteUser()
            is ProfileUiAction.OnClickUserWithdrawal -> validMyMeeting()
            is ProfileUiAction.OnClickRefresh -> userResult.restart()
            is ProfileUiAction.OnShowUserLogoutDialog -> showUserLogoutDialog(uiAction.isShow)
            is ProfileUiAction.OnShowUserDeleteDialog -> showUserDeleteDialog(uiAction.isShow)
        }
    }

    private fun validMyMeeting() {
        viewModelScope.launch {
            runCatching {
                setLoading(true)
                // TODO:: API 변경
                meetingRepository
                    .getMeetings("", 1)
                    .content
            }.onFailure { error ->
                when (error) {
                    is IOException -> setUiEvent(ProfileUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                    else -> setUiEvent(ProfileUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                }
            }.onSuccess { myMeetings ->
                if (myMeetings.isNotEmpty()) {
                    setUiEvent(ProfileUiEvent.NavigateToUserWithdrawalForLeaderChange)
                } else {
                    showUserDeleteDialog(true)
                }
            }.also {
                setLoading(false)
            }
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
            uiState.checkState<ProfileUiState.Success> {
                authRepository
                    .signOut(user.userId)
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                clearUserData()
                            }

                            is Result.Error -> {
                                when (result.exception) {
                                    is IOException -> setUiEvent(ProfileUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                    else -> setUiEvent(ProfileUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun deleteUser() {
        viewModelScope.launch {
            uiState.checkState<ProfileUiState.Success> {
                userRepository
                    .deleteUser()
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                clearUserData()
                            }

                            is Result.Error -> {
                                when (result.exception) {
                                    is IOException -> setUiEvent(ProfileUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                    else -> setUiEvent(ProfileUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                                }
                            }
                        }
                    }
            }
        }
    }

    private suspend fun clearUserData() {
        userRepository.clearMoimStorage()
        setUiEvent(ProfileUiEvent.NavigateToIntro)
    }
}

sealed interface ProfileUiState : UiState {
    data object Loading : ProfileUiState

    data class Success(
        val user: User,
        val isShowUserLogoutDialog: Boolean = false,
        val isShowUserDeleteDialog: Boolean = false,
    ) : ProfileUiState

    data object Error : ProfileUiState
}

sealed interface ProfileUiAction : UiAction {
    data object OnClickProfile : ProfileUiAction

    data object OnClickAlarmSetting : ProfileUiAction

    data object OnClickThemeSetting : ProfileUiAction

    data object OnClickPrivacyPolicy : ProfileUiAction

    data object OnClickLogout : ProfileUiAction

    data object OnClickUserWithdrawal : ProfileUiAction

    data object OnClickUserDelete : ProfileUiAction

    data object OnClickRefresh : ProfileUiAction

    data class OnShowUserLogoutDialog(
        val isShow: Boolean,
    ) : ProfileUiAction

    data class OnShowUserDeleteDialog(
        val isShow: Boolean,
    ) : ProfileUiAction
}

sealed interface ProfileUiEvent : UiEvent {
    data object NavigateToProfileUpdate : ProfileUiEvent

    data object NavigateToAlarmSetting : ProfileUiEvent

    data object NavigateToThemeSetting : ProfileUiEvent

    data object NavigateToPrivacyPolicy : ProfileUiEvent

    data object NavigateToUserWithdrawalForLeaderChange : ProfileUiEvent

    data object NavigateToIntro : ProfileUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : ProfileUiEvent
}
