package com.moim.feature.intro.screen.signin

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.moim.core.common.consts.SOCIAL_TYPE_KAKAO
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.exception.NotFoundException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.token.TokenRepository
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
) : BaseViewModel() {
    fun onUiAction(uiAction: SignInUiAction) {
        when (uiAction) {
            is SignInUiAction.OnClickKakaoLogin -> signInForKakao(uiAction.context)
        }
    }

    private fun signInForKakao(context: Context) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(
                context = context,
                callback = { token, exception -> checkedKakaoToken(token, exception) },
            )
        } else {
            UserApiClient.instance.loginWithKakaoAccount(
                context = context,
                callback = { token, exception -> checkedKakaoToken(token, exception) },
            )
        }
    }

    private fun checkedKakaoToken(
        token: OAuthToken?,
        exception: Throwable?,
    ) {
        when {
            exception != null -> {
                setUiEvent(SignInUiEvent.ShowToastMessage(ToastMessage.SocialLoginErrorMessage))
            }

            token != null -> {
                UserApiClient.instance.me { user, _ ->
                    if (user == null) return@me setUiEvent(SignInUiEvent.ShowToastMessage(ToastMessage.SocialLoginErrorMessage))
                    signIn(token.idToken.toString(), user.kakaoAccount?.email.toString())
                }
            }

            else -> {
                setUiEvent(SignInUiEvent.ShowToastMessage(ToastMessage.SocialLoginErrorMessage))
            }
        }
    }

    private fun signIn(
        accessToken: String,
        email: String,
    ) {
        viewModelScope.launch {
            authRepository
                .signIn(socialType = SOCIAL_TYPE_KAKAO, token = accessToken, email = email)
                .flatMapLatest { tokenRepository.setFcmToken() }
                .asResult()
                .onEach { setLoading(it is Result.Loading) }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            setUiEvent(SignInUiEvent.NavigateToMain)
                        }

                        is Result.Error -> {
                            when (result.exception) {
                                is IOException -> {
                                    setUiEvent(SignInUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                }

                                is NetworkException -> {
                                    when (result.exception) {
                                        is NotFoundException -> {
                                            setUiEvent(
                                                SignInUiEvent.NavigateToSignUp(email = email, token = accessToken),
                                            )
                                        }

                                        else -> {
                                            setUiEvent(SignInUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
}

sealed interface SignInUiAction : UiAction {
    data class OnClickKakaoLogin(
        val context: Context,
    ) : SignInUiAction
}

sealed interface SignInUiEvent : UiEvent {
    data class NavigateToSignUp(
        val email: String,
        val token: String,
    ) : SignInUiEvent

    data object NavigateToMain : SignInUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : SignInUiEvent
}
