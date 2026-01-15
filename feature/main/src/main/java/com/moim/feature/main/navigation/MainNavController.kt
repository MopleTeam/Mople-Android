package com.moim.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.moim.core.ui.route.MainRoute

/**
 * MainNavigator와 MainNavigationState를 통합하는 래퍼 클래스
 */
class MainNavController(
    val navigationState: MainNavigationState,
    val navigator: MainNavigator,
) {
    /**
     * 현재 선택된 MainTab
     */
    val currentTab: MainTab?
        @Composable
        get() = navigationState.currentTab

    /**
     * 시작 라우트
     */
    val startDestination: MainRoute
        get() = navigationState.startRoute

    /**
     * MainTab으로 네비게이션
     */
    fun navigate(tab: MainTab) = navigator.navigate(tab)

    /**
     * Bottom Bar 표시 여부
     */
    @Composable
    fun shouldShowBottomBar() = navigationState.shouldShowBottomBar
}

@Composable
fun rememberMainNavController(): MainNavController {
    val navigationState = rememberMainNavigationState()
    val navigator = remember(navigationState) { MainNavigator(navigationState) }

    return remember(navigationState, navigator) {
        MainNavController(navigationState, navigator)
    }
}
