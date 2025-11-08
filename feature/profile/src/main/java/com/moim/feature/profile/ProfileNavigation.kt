package com.moim.feature.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.moim.core.ui.route.MainRoute

fun NavGraphBuilder.profileScreen(
    padding: PaddingValues,
    navigateToProfileUpdate: () -> Unit,
    navigateToAlarmSetting: () -> Unit,
    navigateToPrivacyPolicy: (String) -> Unit,
    navigateToIntro: () -> Unit,
) {
    composable<MainRoute.Profile> {
        ProfileRoute(
            padding = padding,
            navigateToProfileUpdate = navigateToProfileUpdate,
            navigateToAlarmSetting = navigateToAlarmSetting,
            navigateToPrivacyPolicy = navigateToPrivacyPolicy,
            navigateToIntro = navigateToIntro
        )
    }
}

fun NavController.navigateToProfile(navOptions: NavOptions? = null) {
    this.navigate(MainRoute.Profile, navOptions)
}