package com.moim.feature.profile

import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.asResult
import com.moim.core.common.result.data
import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.util.cancelIfActive
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.profile.model.ProfileIntent
import com.moim.feature.profile.model.ProfileSideEffect
import com.moim.feature.profile.model.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val meetingRepository: MeetingRepository,
) : MVIViewModel<ProfileState, ProfileSideEffect>(ProfileState()) {
    private var userJob: Job? = null

    override suspend fun Syntax<ProfileState, ProfileSideEffect>.onContainerCreate() {
        observeUser()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is ProfileIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is ProfileIntent.ProfileClick -> {
                    postSideEffect(ProfileSideEffect.NavigateToProfileUpdate)
                }

                is ProfileIntent.AlarmSettingClick -> {
                    postSideEffect(ProfileSideEffect.NavigateToAlarmSetting)
                }

                is ProfileIntent.ThemeSettingClick -> {
                    postSideEffect(ProfileSideEffect.NavigateToThemeSetting)
                }

                is ProfileIntent.PrivacyPolicyClick -> {
                    postSideEffect(ProfileSideEffect.NavigateToPrivacyPolicy)
                }

                is ProfileIntent.RefreshClick -> {
                    observeUser()
                }

                is ProfileIntent.LogoutClick -> {
                    logout()
                }

                is ProfileIntent.UserDeleteClick -> {
                    deleteUser()
                }

                is ProfileIntent.UserWithdrawalClick -> {
                    validMyMeeting()
                }

                is ProfileIntent.UserLogoutDialogShow -> {
                    reduce { state.copy(isShowUserLogoutDialog = intent.isShow) }
                }

                is ProfileIntent.UserDeleteDialogShow -> {
                    reduce { state.copy(isShowUserDeleteDialog = intent.isShow) }
                }
            }
        }
    }

    // 로컬 저장소 Flow라 프로필 수정 후 돌아왔을 때도 자동 반영된다.
    private fun observeUser() {
        userJob.cancelIfActive()
        userJob =
            userRepository
                .getUser()
                .asResult()
                .onEach { result -> intent { reduce { state.copy(user = result) } } }
                .launchIn(viewModelScope)
    }

    private suspend fun Syntax<ProfileState, ProfileSideEffect>.validMyMeeting() {
        setLoading(true)

        try {
            val myMeetings =
                meetingRepository
                    .getMeetingsForHost("", 100)
                    .content
                    .filter { it.memberCount > 1 }

            if (myMeetings.isNotEmpty()) {
                postSideEffect(ProfileSideEffect.NavigateToUserWithdrawalForLeaderChange)
            } else {
                reduce { state.copy(isShowUserDeleteDialog = true) }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<ProfileState, ProfileSideEffect>.logout() {
        val user = state.user.data ?: return

        setLoading(true)

        try {
            authRepository.signOut(user.userId)
            clearUserData()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<ProfileState, ProfileSideEffect>.deleteUser() {
        setLoading(true)

        try {
            userRepository.deleteUser()
            clearUserData()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<ProfileState, ProfileSideEffect>.clearUserData() {
        userRepository.clearMoimStorage()
        postSideEffect(ProfileSideEffect.NavigateToIntro)
    }

    private suspend fun Syntax<ProfileState, ProfileSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(ProfileSideEffect.ShowToastMessage(message))
    }
}
