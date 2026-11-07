package com.moim.feature.intro.screen.signup

import com.moim.core.common.consts.PATTERN_NICKNAME
import com.moim.core.common.consts.SOCIAL_TYPE_KAKAO
import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.token.TokenRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.IntroRoute
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.intro.screen.signup.model.SignUpIntent
import com.moim.feature.intro.screen.signup.model.SignUpSideEffect
import com.moim.feature.intro.screen.signup.model.SignUpState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import java.util.regex.Pattern

@HiltViewModel(assistedFactory = SignUpViewModel.Factory::class)
class SignUpViewModel @AssistedInject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    @Assisted signUpRoute: IntroRoute.SignUp,
) : MVIViewModel<SignUpState, SignUpSideEffect>(signUpRoute.asState()) {
    override fun onIntent(intent: Intent) {
        if (intent !is SignUpIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is SignUpIntent.SignUpClick -> {
                    signUp()
                }

                is SignUpIntent.DuplicatedCheckClick -> {
                    validateDuplicateNickname()
                }

                is SignUpIntent.PhotoPickerClick -> {
                    postSideEffect(SignUpSideEffect.NavigateToPhotoPicker)
                }

                is SignUpIntent.ProfileUrlChange -> {
                    reduce { state.copy(profileUrl = intent.profileUrl) }
                }

                is SignUpIntent.NicknameChange -> {
                    updateNickname(intent.nickname)
                }

                is SignUpIntent.ProfileEditDialogShow -> {
                    reduce { state.copy(isShowProfileEditDialog = intent.isShow) }
                }
            }
        }
    }

    private suspend fun Syntax<SignUpState, SignUpSideEffect>.updateNickname(nickname: String) {
        val trimNickname = nickname.trim()

        reduce {
            state.copy(
                nickname = trimNickname,
                isDuplicatedName = null,
                isRegexError = if (trimNickname.isEmpty()) false else Pattern.matches(PATTERN_NICKNAME, nickname).not(),
                enableSignUp = false,
            )
        }
    }

    private suspend fun Syntax<SignUpState, SignUpSideEffect>.validateDuplicateNickname() {
        if (state.nickname.isEmpty() || state.isRegexError) return

        setLoading(true)

        try {
            val isDuplicated = userRepository.checkedNickname(state.nickname)

            reduce { state.copy(isDuplicatedName = isDuplicated, enableSignUp = isDuplicated.not()) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            postSideEffect(SignUpSideEffect.ShowToastMessage(e.asToastMessage()))
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<SignUpState, SignUpSideEffect>.signUp() {
        setLoading(true)

        try {
            authRepository.signUp(
                socialType = SOCIAL_TYPE_KAKAO,
                token = state.token,
                email = state.email,
                nickname = state.nickname,
                profileUrl = state.profileUrl,
            )
            tokenRepository.setFcmToken()

            postSideEffect(SignUpSideEffect.NavigateToMain)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            postSideEffect(SignUpSideEffect.ShowToastMessage(e.asToastMessage()))
        } finally {
            setLoading(false)
        }
    }

    private fun Throwable.asToastMessage() = if (this is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage

    @AssistedFactory
    interface Factory {
        fun create(signUpRoute: IntroRoute.SignUp): SignUpViewModel
    }
}

private fun IntroRoute.SignUp.asState() =
    SignUpState(
        email = email,
        token = token,
    )
