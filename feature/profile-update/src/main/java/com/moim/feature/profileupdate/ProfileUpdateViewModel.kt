package com.moim.feature.profileupdate

import com.moim.core.common.consts.PATTERN_NICKNAME
import com.moim.core.common.result.Result
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.profileupdate.model.ProfileUpdateIntent
import com.moim.feature.profileupdate.model.ProfileUpdateSideEffect
import com.moim.feature.profileupdate.model.ProfileUpdateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class ProfileUpdateViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : MVIViewModel<ProfileUpdateState, ProfileUpdateSideEffect>(ProfileUpdateState()) {
    override suspend fun Syntax<ProfileUpdateState, ProfileUpdateSideEffect>.onContainerCreate() {
        loadUser()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is ProfileUpdateIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is ProfileUpdateIntent.BackClick -> {
                    postSideEffect(ProfileUpdateSideEffect.NavigateToBack)
                }

                is ProfileUpdateIntent.RefreshClick -> {
                    loadUser()
                }

                is ProfileUpdateIntent.PhotoPickerClick -> {
                    postSideEffect(ProfileUpdateSideEffect.NavigateToPhotoPicker)
                }

                is ProfileUpdateIntent.ProfileUpdateClick -> {
                    updateUser()
                }

                is ProfileUpdateIntent.DuplicatedCheckClick -> {
                    validateDuplicateNickname()
                }

                is ProfileUpdateIntent.ProfileUrlChange -> {
                    setProfileUrl(intent.profileUrl)
                }

                is ProfileUpdateIntent.NicknameChange -> {
                    setNickname(intent.nickname)
                }

                is ProfileUpdateIntent.ProfileEditDialogShow -> {
                    reduce { state.copy(isShowProfileEditDialog = intent.isShow) }
                }
            }
        }
    }

    private fun loadUser() {
        intent {
            reduce { state.copy(user = Result.Loading) }

            try {
                val user = userRepository.getUser().first()

                reduce {
                    state.copy(
                        user = Result.Success(user),
                        profileUrl = user.profileUrl,
                        nickname = user.nickname,
                        enableProfileUpdate = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                reduce { state.copy(user = Result.Error(e)) }
            }
        }
    }

    private suspend fun Syntax<ProfileUpdateState, ProfileUpdateSideEffect>.setProfileUrl(photoUrl: String?) {
        val enableProfileUpdate = (state.isDuplicatedName == true).not() && state.isRegexError.not()

        reduce {
            state.copy(
                profileUrl = photoUrl,
                enableProfileUpdate = enableProfileUpdate,
            )
        }
    }

    private suspend fun Syntax<ProfileUpdateState, ProfileUpdateSideEffect>.setNickname(nickname: String) {
        val trimNickname = nickname.trim()

        reduce {
            state.copy(
                nickname = trimNickname,
                isDuplicatedName = null,
                isRegexError = if (trimNickname.isEmpty()) false else Pattern.matches(PATTERN_NICKNAME, nickname).not(),
                enableProfileUpdate = false,
            )
        }
    }

    private suspend fun Syntax<ProfileUpdateState, ProfileUpdateSideEffect>.validateDuplicateNickname() {
        val nickname = state.nickname
        if (state.currentNickname == nickname || nickname.isEmpty() || state.isRegexError) return

        setLoading(true)

        try {
            val isDuplicated = userRepository.checkedNickname(nickname)

            reduce {
                state.copy(
                    isDuplicatedName = isDuplicated,
                    enableProfileUpdate = isDuplicated.not(),
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<ProfileUpdateState, ProfileUpdateSideEffect>.updateUser() {
        setLoading(true)

        try {
            userRepository.updateUser(
                profileUrl = state.profileUrl,
                nickname = state.nickname,
            )

            postSideEffect(ProfileUpdateSideEffect.NavigateToBack)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<ProfileUpdateState, ProfileUpdateSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(ProfileUpdateSideEffect.ShowToastMessage(message))
    }
}
