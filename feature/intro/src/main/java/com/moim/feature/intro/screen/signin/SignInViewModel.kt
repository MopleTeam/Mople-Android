package com.moim.feature.intro.screen.signin

import com.moim.core.common.consts.SOCIAL_TYPE_KAKAO
import com.moim.core.common.exception.NotFoundException
import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.token.TokenRepository
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.intro.kakao.KakaoLoginClient
import com.moim.feature.intro.screen.signin.model.SignInIntent
import com.moim.feature.intro.screen.signin.model.SignInSideEffect
import com.moim.feature.intro.screen.signin.model.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import org.orbitmvi.orbit.syntax.Syntax
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val kakaoLoginClient: KakaoLoginClient,
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
) : MVIViewModel<SignInState, SignInSideEffect>(SignInState) {
    override fun onIntent(intent: Intent) {
        if (intent !is SignInIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is SignInIntent.KakaoLoginClick -> {
                    signInForKakao()
                }
            }
        }
    }

    private suspend fun Syntax<SignInState, SignInSideEffect>.signInForKakao() {
        val account =
            try {
                kakaoLoginClient.login()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                postSideEffect(SignInSideEffect.ShowToastMessage(ToastMessage.SocialLoginErrorMessage))
                return
            }

        signIn(
            accessToken = account.token,
            email = account.email,
        )
    }

    private suspend fun Syntax<SignInState, SignInSideEffect>.signIn(
        accessToken: String,
        email: String,
    ) {
        setLoading(true)

        try {
            authRepository.signIn(
                socialType = SOCIAL_TYPE_KAKAO,
                token = accessToken,
                email = email,
            )
            tokenRepository.setFcmToken()

            postSideEffect(SignInSideEffect.NavigateToMain)
        } catch (e: CancellationException) {
            throw e
        } catch (_: NotFoundException) {
            postSideEffect(
                SignInSideEffect.NavigateToSignUp(
                    email = email,
                    token = accessToken,
                ),
            )
        } catch (e: Exception) {
            Timber.e("error $e")
            val message = if (e is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
            postSideEffect(SignInSideEffect.ShowToastMessage(message))
        } finally {
            setLoading(false)
        }
    }
}
