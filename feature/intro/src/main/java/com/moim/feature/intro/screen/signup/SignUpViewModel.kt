package com.moim.feature.intro.screen.signup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.common.consts.PATTERN_NICKNAME
import com.moim.core.common.consts.SOCIAL_TYPE_KAKAO
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.token.TokenRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okio.IOException
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : BaseViewModel() {

    private val email
        get() = savedStateHandle.get<String>(KEY_EMAIL) ?: ""

    private val token
        get() = savedStateHandle.get<String>(KEY_TOKEN) ?: ""

    init {
        setUiState(SignUpUiState.SignUp())
    }

    fun onUiAction(uiAction: SignUpUiAction) {
        when (uiAction) {
            is SignUpUiAction.OnClickDuplicatedCheck -> validateDuplicateNickname()
            is SignUpUiAction.OnClickSignUp -> signUp()
            is SignUpUiAction.OnShowProfileEditDialog -> showProfileEditDialog(uiAction.isShow)
            is SignUpUiAction.OnChangeProfileUrl -> setProfileUrl(uiAction.profileUrl)
            is SignUpUiAction.OnChangeNickname -> setNickname(uiAction.nickname)
            is SignUpUiAction.OnNavigatePhotoPicker -> setUiEvent(SignUpUiEvent.NavigateToPhotoPicker)
        }
    }

    private fun showProfileEditDialog(isShow: Boolean) {
        uiState.checkState<SignUpUiState.SignUp> {
            setUiState(copy(isShowProfileEditDialog = isShow))
        }
    }

    private fun setProfileUrl(photoUrl: String? = null) {
        uiState.checkState<SignUpUiState.SignUp> {
            setUiState(copy(profileUrl = photoUrl))
        }
    }

    private fun setNickname(nickname: String) {
        uiState.checkState<SignUpUiState.SignUp> {
            val trimNickname = nickname.trim()

            setUiState(
                copy(
                    nickname = trimNickname,
                    isDuplicatedName = null,
                    isRegexError = if (trimNickname.isEmpty()) false else Pattern.matches(PATTERN_NICKNAME, nickname).not(),
                    enableSignUp = false
                )
            )
        }
    }

    private fun validateDuplicateNickname() {
        viewModelScope.launch {
            uiState.checkState<SignUpUiState.SignUp> {
                if (nickname.isEmpty() || isRegexError) return@checkState
                userRepository
                    .checkedNickname(nickname)
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiState(copy(isDuplicatedName = result.data, enableSignUp = result.data.not()))
                            is Result.Error -> when (result.exception) {
                                is IOException -> setUiEvent(SignUpUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                else -> setUiEvent(SignUpUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                            }
                        }
                    }
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            uiState.checkState<SignUpUiState.SignUp> {
                authRepository
                    .signUp(
                        socialType = SOCIAL_TYPE_KAKAO,
                        token = token,
                        email = email,
                        nickname = nickname,
                        profileUrl = profileUrl
                    )
                    .flatMapLatest { tokenRepository.setFcmToken() }
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiEvent(SignUpUiEvent.NavigateToMain)
                            is Result.Error -> when (result.exception) {
                                is IOException -> setUiEvent(SignUpUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                else -> setUiEvent(SignUpUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                            }
                        }
                    }
            }
        }
    }

    companion object {
        private const val KEY_EMAIL = "email"
        private const val KEY_TOKEN = "token"
    }
}

sealed interface SignUpUiState : UiState {
    data class SignUp(
        val profileUrl: String? = null,
        val nickname: String = "",
        val isDuplicatedName: Boolean? = null,
        val isRegexError: Boolean = false,
        val enableSignUp: Boolean = false,
        val isShowProfileEditDialog: Boolean = false
    ) : SignUpUiState
}

sealed interface SignUpUiAction : UiAction {
    data object OnClickSignUp : SignUpUiAction

    data object OnClickDuplicatedCheck : SignUpUiAction

    data class OnChangeProfileUrl(
        val profileUrl: String?
    ) : SignUpUiAction

    data class OnChangeNickname(
        val nickname: String
    ) : SignUpUiAction

    data class OnShowProfileEditDialog(
        val isShow: Boolean
    ) : SignUpUiAction

    data object OnNavigatePhotoPicker : SignUpUiAction
}

sealed interface SignUpUiEvent : UiEvent {
    data object NavigateToPhotoPicker : SignUpUiEvent

    data object NavigateToMain : SignUpUiEvent

    data class ShowToastMessage(val message: ToastMessage) : SignUpUiEvent
}