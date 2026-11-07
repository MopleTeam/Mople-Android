package com.moim.feature.userwithdrawalforleaderchange.model

import com.moim.core.ui.mvi.Intent

sealed interface UserWithdrawalForLeaderChangeIntent : Intent {
    data object BackClick : UserWithdrawalForLeaderChangeIntent

    data object RefreshClick : UserWithdrawalForLeaderChangeIntent

    data object NextPageLoad : UserWithdrawalForLeaderChangeIntent

    data object UserDeleteClick : UserWithdrawalForLeaderChangeIntent

    data class MeetingClick(
        val meetId: String,
    ) : UserWithdrawalForLeaderChangeIntent

    data class UserDeleteDialogShow(
        val isShow: Boolean,
    ) : UserWithdrawalForLeaderChangeIntent
}
