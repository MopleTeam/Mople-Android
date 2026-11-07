package com.moim.feature.userwithdrawalforleaderchange.model

sealed interface UserWithdrawalForLeaderChangeSideEffect {
    data object NavigateToBack : UserWithdrawalForLeaderChangeSideEffect

    data object NavigateToExit : UserWithdrawalForLeaderChangeSideEffect

    data class NavigateToParticipantsForLeaderChange(
        val meetId: String,
    ) : UserWithdrawalForLeaderChangeSideEffect

    data object ShowNetworkErrorMessage : UserWithdrawalForLeaderChangeSideEffect

    data object ShowServerErrorMessage : UserWithdrawalForLeaderChangeSideEffect
}
