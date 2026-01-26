package com.moim.feature.main.navigation

import androidx.annotation.DrawableRes
import androidx.navigation3.runtime.NavKey
import com.moim.core.common.model.Comment
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.route.MainRoute

/**
 * 네비게이션 이벤트를 처리하고 NavigationState를 업데이트
 */
class MainNavigator(
    val state: MainNavigationState,
) {
    /**
     * 라우트로 네비게이션
     */
    fun navigate(route: NavKey) {
        when (route) {
            // MainRoute이면 탭 전환
            is MainRoute -> {
                state.topLevelRoute = route
            }

            // DetailRoute이면 현재 스택에 추가
            else -> {
                state.backStacks[state.topLevelRoute]?.add(route)
            }
        }
    }

    /**
     * MainTab으로 네비게이션 (UI에서 탭 클릭 시)
     */
    fun navigate(tab: MainTab) {
        navigate(tab.route)
    }

    /**
     * 뒤로 가기
     */
    fun goBack() {
        val currentStack =
            state.backStacks[state.topLevelRoute] ?: error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.lastOrNull()

        when {
            currentRoute == null || currentRoute == state.topLevelRoute -> {
                if (state.topLevelRoute != state.startRoute) {
                    state.topLevelRoute = state.startRoute
                }
            }

            else -> {
                currentStack.removeLastOrNull()
            }
        }
    }

    // === Main Tab Navigation ===
    fun navigateToHome() = navigate(MainRoute.Home)

    fun navigateToMeeting() = navigate(MainRoute.Meeting)

    fun navigateToCalendar() = navigate(MainRoute.Calendar)

    fun navigateToProfile() = navigate(MainRoute.Profile)

    // === Detail Navigation ===
    fun navigateToMeetingDetail(meetingId: String) = navigate(DetailRoute.MeetingDetail(meetingId))

    fun navigateToMeetingWrite(meeting: Meeting? = null) = navigate(DetailRoute.MeetingWrite(meeting))

    fun navigateToMeetingSetting(meeting: Meeting) = navigate(DetailRoute.MeetingSetting(meeting))

    fun navigateToMapDetail(
        placeName: String,
        address: String,
        latitude: Double,
        longitude: Double,
    ) = navigate(DetailRoute.MapDetail(placeName, address, latitude, longitude))

    fun navigateToPlanDetail(viewIdType: ViewIdType) = navigate(DetailRoute.PlanDetail(viewIdType))

    fun navigateToCommentDetail(
        meetId: String,
        postId: String,
        comment: Comment? = null,
    ) = navigate(DetailRoute.CommentDetail(meetId, postId, comment))

    fun navigateToPlanWrite(planItem: PlanItem? = null) = navigate(DetailRoute.PlanWrite(planItem))

    fun navigateToReviewWrite(
        postId: String,
        isUpdated: Boolean = false,
    ) = navigate(DetailRoute.ReviewWrite(postId, isUpdated))

    fun navigateToParticipantList(viewIdType: ViewIdType) = navigate(DetailRoute.ParticipantList(viewIdType))

    fun navigateToParticipantListForLeaderChange(viewIdType: ViewIdType.MeetId) =
        navigate(DetailRoute.ParticipantListForLeaderChange(viewIdType))

    fun navigateToUserWithdrawalForLeaderChange() = navigate(DetailRoute.UserWithdrawalForLeaderChange)

    fun navigateToImageViewer(
        title: String,
        images: List<String>,
        position: Int,
        @DrawableRes defaultImage: Int? = null,
    ) = navigate(DetailRoute.ImageViewer(title, images, position, defaultImage))

    fun navigateToProfileUpdate() = navigate(DetailRoute.ProfileUpdate)

    fun navigateToAlarm() = navigate(DetailRoute.Alarm)

    fun navigateToAlarmSetting() = navigate(DetailRoute.AlarmSetting)

    fun navigateToThemeSetting() = navigate(DetailRoute.ThemeSetting)

    fun navigateToWebView(webUrl: String) = navigate(DetailRoute.WebView(webUrl))
}
