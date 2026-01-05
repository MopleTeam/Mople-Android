package com.moim.feature.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavKeySerializer
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import com.moim.core.ui.route.MainRoute

/**
 * NavigationState를 생성하고 config 변경 및 프로세스 종료에도 유지
 */
@Composable
fun rememberMainNavigationState(
    startRoute: MainRoute = MainRoute.Home,
    topLevelRoutes: List<MainRoute> = MainTab.routes
): MainNavigationState {
    val topLevelRoute = rememberSerializable(
        startRoute, topLevelRoutes,
        serializer = MutableStateSerializer(NavKeySerializer())
    ) {
        mutableStateOf(startRoute)
    }

    val backStacks = topLevelRoutes.associateWith { key ->
        rememberNavBackStack(key)
    }

    return remember(startRoute, topLevelRoutes) {
        MainNavigationState(
            startRoute = startRoute,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks,
            topLevelRoutes = topLevelRoutes
        )
    }
}

/**
 * 메인 네비게이션 상태 홀더
 */
class MainNavigationState(
    val startRoute: MainRoute,
    topLevelRoute: MutableState<MainRoute>,
    val backStacks: Map<MainRoute, NavBackStack<NavKey>>,
    private val topLevelRoutes: List<MainRoute>
) {
    var topLevelRoute: MainRoute by topLevelRoute

    /**
     * 현재 사용 중인 스택 목록
     */
    val stacksInUse: List<MainRoute>
        get() = if (topLevelRoute == startRoute) {
            listOf(startRoute)
        } else {
            listOf(startRoute, topLevelRoute)
        }

    /**
     * 현재 백스택의 최상위 라우트
     */
    val currentRoute: NavKey?
        get() = backStacks[topLevelRoute]?.lastOrNull()

    /**
     * 현재 라우트가 Top Level(탭 메인 화면)인지 확인
     */
    val isAtTopLevel: Boolean
        get() = currentRoute in topLevelRoutes

    /**
     * Bottom Bar 표시 여부
     */
    val shouldShowBottomBar: Boolean
        get() = isAtTopLevel

    /**
     * 현재 선택된 MainTab 반환
     */
    val currentTab: MainTab?
        get() = MainTab.find { it == topLevelRoute }
}

/**
 * NavigationState를 NavEntry 리스트로 변환
 */
@Composable
fun MainNavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>
): SnapshotStateList<NavEntry<NavKey>> {
    val decoratedEntries = backStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator()
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider
        )
    }

    return stacksInUse
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}