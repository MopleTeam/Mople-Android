package com.moim.feature.profileupdate

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.moim.core.common.consts.PATTERN_NICKNAME
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.designsystem.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class ProfileUpdateViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val userResult = loadDataSignal
        .flatMapLatest { userRepository.getUser().asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            userResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(ProfileUpdateUiState.Loading)
                    is Result.Success -> {
                        val user = result.data

                        setUiState(
                            ProfileUpdateUiState.Success(
                                profileUrl = user.profileUrl,
                                nickname = user.nickname,
                                enableProfileUpdate = false
                            )
                        )
                    }

                    is Result.Error -> setUiState(ProfileUpdateUiState.Error)
                }
            }
        }
    }

    fun onUiAction(uiAction: ProfileUpdateUiAction) {
        when (uiAction) {
            is ProfileUpdateUiAction.OnClickBack -> setUiEvent(ProfileUpdateUiEvent.NavigateToBack)
            is ProfileUpdateUiAction.OnClickProfileUpdate -> updateUser()
            is ProfileUpdateUiAction.OnClickDuplicatedCheck -> validateDuplicateNickname()
            is ProfileUpdateUiAction.OnClickRefresh -> onRefresh()
            is ProfileUpdateUiAction.OnChangeProfileUrl -> setProfileUrl(uiAction.profileUrl)
            is ProfileUpdateUiAction.OnChangeNickname -> setNickname(uiAction.nickname)
            is ProfileUpdateUiAction.OnShowProfileEditDialog -> showProfileEditDialog(uiAction.isShow)
            is ProfileUpdateUiAction.OnNavigatePhotoPicker -> setUiEvent(ProfileUpdateUiEvent.NavigateToPhotoPicker)
        }
    }

    private fun showProfileEditDialog(isShow: Boolean) {
        uiState.checkState<ProfileUpdateUiState.Success> {
            setUiState(copy(isShowProfileEditDialog = isShow))
        }
    }

    private fun setProfileUrl(photoUrl: String? = null) {
        uiState.checkState<ProfileUpdateUiState.Success> {
            val enableProfileUpdate = (isDuplicatedName == true).not() && isRegexError.not()
            setUiState(copy(profileUrl = photoUrl, enableProfileUpdate = enableProfileUpdate))
        }
    }

    private fun setNickname(nickname: String) {
        uiState.checkState<ProfileUpdateUiState.Success> {
            val trimNickname = nickname.trim()

            setUiState(
                copy(
                    nickname = trimNickname,
                    isDuplicatedName = null,
                    isRegexError = if (trimNickname.isEmpty()) false else Pattern.matches(PATTERN_NICKNAME, nickname).not(),
                    enableProfileUpdate = false
                )
            )
        }
    }

    private fun validateDuplicateNickname() {
        viewModelScope.launch {
            uiState.checkState<ProfileUpdateUiState.Success> {
                if (nickname.isEmpty() || isRegexError) return@checkState
                userRepository
                    .checkedNickname(nickname)
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiState(copy(isDuplicatedName = result.data, enableProfileUpdate = result.data.not()))
                            is Result.Error -> setUiEvent(ProfileUpdateUiEvent.ShowToastMessage(R.string.common_error_disconnection))
                        }
                    }
            }
        }
    }

    private fun updateUser() {
        viewModelScope.launch {
            uiState.checkState<ProfileUpdateUiState.Success> {
                userRepository.updateUser(profileUrl = profileUrl, nickname = nickname)
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiEvent(ProfileUpdateUiEvent.NavigateToBack)
                            is Result.Error -> setUiEvent(ProfileUpdateUiEvent.ShowToastMessage(R.string.common_error_disconnection))
                        }
                    }
            }
        }
    }
}

sealed interface ProfileUpdateUiState : UiState {
    data object Loading : ProfileUpdateUiState

    data class Success(
        val profileUrl: String? = null,
        val nickname: String = "",
        val isDuplicatedName: Boolean? = null,
        val isRegexError: Boolean = false,
        val isShowProfileEditDialog: Boolean = false,
        val enableProfileUpdate: Boolean = false,
    ) : ProfileUpdateUiState

    data object Error : ProfileUpdateUiState
}

sealed interface ProfileUpdateUiAction : UiAction {
    data object OnClickBack : ProfileUpdateUiAction
    data object OnClickProfileUpdate : ProfileUpdateUiAction
    data object OnClickDuplicatedCheck : ProfileUpdateUiAction
    data object OnClickRefresh : ProfileUpdateUiAction
    data class OnChangeProfileUrl(val profileUrl: String?) : ProfileUpdateUiAction
    data class OnChangeNickname(val nickname: String) : ProfileUpdateUiAction
    data class OnShowProfileEditDialog(val isShow: Boolean) : ProfileUpdateUiAction
    data object OnNavigatePhotoPicker : ProfileUpdateUiAction

}

sealed interface ProfileUpdateUiEvent : UiEvent {
    data object NavigateToBack : ProfileUpdateUiEvent
    data object NavigateToPhotoPicker : ProfileUpdateUiEvent
    data class ShowToastMessage(@StringRes val messageRes: Int) : ProfileUpdateUiEvent
}
