package com.moim.feature.main.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.route.MainRoute
import com.moim.feature.alarm.AlarmRoute
import com.moim.feature.alarmsetting.AlarmSettingRoute
import com.moim.feature.calendar.CalendarRoute
import com.moim.feature.commentdetail.CommentDetailRoute
import com.moim.feature.commentdetail.CommentDetailViewModel
import com.moim.feature.home.HomeRoute
import com.moim.feature.imageviewer.ImageViewerRoute
import com.moim.feature.imageviewer.ImageViewerViewModel
import com.moim.feature.mapdetail.MapDetailRoute
import com.moim.feature.mapdetail.MapDetailViewModel
import com.moim.feature.meeting.MeetingRoute
import com.moim.feature.meetingdetail.MeetingDetailRoute
import com.moim.feature.meetingdetail.MeetingDetailViewModel
import com.moim.feature.meetingsetting.MeetingSettingRoute
import com.moim.feature.meetingsetting.MeetingSettingViewModel
import com.moim.feature.meetingwrite.MeetingWriteRoute
import com.moim.feature.meetingwrite.MeetingWriteViewModel
import com.moim.feature.participantlist.ParticipantListRoute
import com.moim.feature.participantlist.ParticipantListViewModel
import com.moim.feature.participantlistforleaderchange.ParticipantListForLeaderChangeRoute
import com.moim.feature.participantlistforleaderchange.ParticipantListForLeaderChangeViewModel
import com.moim.feature.plandetail.PlanDetailRoute
import com.moim.feature.plandetail.PlanDetailViewModel
import com.moim.feature.planwrite.PlanWriteRoute
import com.moim.feature.planwrite.PlanWriteViewModel
import com.moim.feature.profile.ProfileRoute
import com.moim.feature.profileupdate.ProfileUpdateRoute
import com.moim.feature.reviewwrite.ReviewWriteRoute
import com.moim.feature.reviewwrite.ReviewWriteViewModel
import com.moim.feature.themesetting.ThemeSettingRoute
import com.moim.feature.userwithdrawalforleaderchange.UserWithdrawalForLeaderChangeRoute
import com.moim.feature.webview.WebViewRoute
import com.moim.feature.webview.WebViewViewModel

fun EntryProviderScope<NavKey>.homeScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<MainRoute.Home> {
        HomeRoute(
            padding = paddingValues,
            navigateToAlarm = navigator::navigateToAlarm,
            navigateToMeetingWrite = navigator::navigateToMeetingWrite,
            navigateToPlanWrite = navigator::navigateToPlanWrite,
            navigateToCalendar = navigator::navigateToCalendar,
            navigateToPlanDetail = navigator::navigateToPlanDetail,
        )
    }
}

fun EntryProviderScope<NavKey>.meetingScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<MainRoute.Meeting> {
        MeetingRoute(
            padding = paddingValues,
            navigateToMeetingWrite = navigator::navigateToMeetingWrite,
            navigateToMeetingDetail = navigator::navigateToMeetingDetail,
        )
    }
}

fun EntryProviderScope<NavKey>.calendarScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<MainRoute.Calendar> {
        CalendarRoute(
            padding = paddingValues,
            navigateToPlanDetail = navigator::navigateToPlanDetail,
        )
    }
}

fun EntryProviderScope<NavKey>.profileScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
    navigateToIntro: () -> Unit,
) {
    entry<MainRoute.Profile> {
        ProfileRoute(
            padding = paddingValues,
            navigateToProfileUpdate = navigator::navigateToProfileUpdate,
            navigateToAlarmSetting = navigator::navigateToAlarmSetting,
            navigateToPrivacyPolicy = navigator::navigateToWebView,
            navigateToThemeSetting = navigator::navigateToThemeSetting,
            navigateToUserWithdrawalForLeaderChange = navigator::navigateToUserWithdrawalForLeaderChange,
            navigateToIntro = navigateToIntro,
        )
    }
}

// === Detail Screens ===
fun EntryProviderScope<NavKey>.meetingDetailScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.MeetingDetail> { key ->
        MeetingDetailRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            navigateToPlanWrite = navigator::navigateToPlanWrite,
            navigateToPlanDetail = navigator::navigateToPlanDetail,
            navigateToMeetingSetting = navigator::navigateToMeetingSetting,
            navigateToImageViewer = navigator::navigateToImageViewer,
            viewModel =
                hiltViewModel<MeetingDetailViewModel, MeetingDetailViewModel.Factory>(
                    key = key.meetingId,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.meetingWriteScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.MeetingWrite> { key ->
        MeetingWriteRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            viewModel =
                hiltViewModel<MeetingWriteViewModel, MeetingWriteViewModel.Factory>(
                    key = key.meeting?.id,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.meetingSettingScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.MeetingSetting> { key ->
        MeetingSettingRoute(
            padding = paddingValues,
            navigateToBack = { shouldPopTwice ->
                navigator.goBack()
                if (shouldPopTwice) navigator.goBack()
            },
            navigateToParticipants = navigator::navigateToParticipantList,
            navigateToParticipantsForLeaderChange = navigator::navigateToParticipantListForLeaderChange,
            navigateToMeetingWrite = navigator::navigateToMeetingWrite,
            viewModel =
                hiltViewModel<MeetingSettingViewModel, MeetingSettingViewModel.Factory>(
                    key = key.meeting.id,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.mapDetailScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.MapDetail> { key ->
        MapDetailRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            viewModel =
                hiltViewModel<MapDetailViewModel, MapDetailViewModel.Factory>(
                    key = "${key.latitude}_${key.longitude}",
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.planDetailScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.PlanDetail> { key ->
        PlanDetailRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            navigateToMapDetail = navigator::navigateToMapDetail,
            navigateToParticipants = navigator::navigateToParticipantList,
            navigateToPlanWrite = navigator::navigateToPlanWrite,
            navigateToReviewWrite = navigator::navigateToReviewWrite,
            navigateToCommentDetail = navigator::navigateToCommentDetail,
            navigateToImageViewer = navigator::navigateToImageViewer,
            viewModel =
                hiltViewModel<PlanDetailViewModel, PlanDetailViewModel.Factory>(
                    key = key.viewIdType.id,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.commentDetailScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.CommentDetail> { key ->
        CommentDetailRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            navigateToImageViewer = navigator::navigateToImageViewer,
            viewModel =
                hiltViewModel<CommentDetailViewModel, CommentDetailViewModel.Factory>(
                    key = key.postId,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.planWriteScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.PlanWrite> { key ->
        PlanWriteRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            viewModel =
                hiltViewModel<PlanWriteViewModel, PlanWriteViewModel.Factory>(
                    key = key.planItem?.postId,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.reviewWriteScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.ReviewWrite> { key ->
        ReviewWriteRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            navigateToParticipants = navigator::navigateToParticipantList,
            viewModel =
                hiltViewModel<ReviewWriteViewModel, ReviewWriteViewModel.Factory>(
                    key = key.postId,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.participantListScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.ParticipantList> { key ->
        ParticipantListRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            navigateToImageViewer = navigator::navigateToImageViewer,
            viewModel =
                hiltViewModel<ParticipantListViewModel, ParticipantListViewModel.Factory>(
                    key = key.viewIdType.id,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.participantListForLeaderChangeScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.ParticipantListForLeaderChange> { key ->
        ParticipantListForLeaderChangeRoute(
            padding = paddingValues,
            navigateToBack = { shouldPopTwice ->
                navigator.goBack()
                if (shouldPopTwice) navigator.goBack()
            },
            navigateToImageViewer = navigator::navigateToImageViewer,
            viewModel =
                hiltViewModel<ParticipantListForLeaderChangeViewModel, ParticipantListForLeaderChangeViewModel.Factory>(
                    key = key.meetId.id,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.userWithdrawalForLeaderChangeScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
    navigateToIntro: () -> Unit,
) {
    entry<DetailRoute.UserWithdrawalForLeaderChange> {
        UserWithdrawalForLeaderChangeRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            navigateToExit = navigateToIntro,
            navigateToParticipantsForLeaderChange = navigator::navigateToParticipantListForLeaderChange,
        )
    }
}

fun EntryProviderScope<NavKey>.imageViewerScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.ImageViewer> { key ->
        ImageViewerRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            viewModel =
                hiltViewModel<ImageViewerViewModel, ImageViewerViewModel.Factory>(
                    key = key.title,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}

fun EntryProviderScope<NavKey>.profileUpdateScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.ProfileUpdate> {
        ProfileUpdateRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
        )
    }
}

fun EntryProviderScope<NavKey>.alarmScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.Alarm> {
        AlarmRoute(
            padding = paddingValues,
            navigateToMeetingDetail = navigator::navigateToMeetingDetail,
            navigateToPlanDetail = navigator::navigateToPlanDetail,
            navigateToBack = navigator::goBack,
        )
    }
}

fun EntryProviderScope<NavKey>.alarmSettingScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.AlarmSetting> {
        AlarmSettingRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
        )
    }
}

fun EntryProviderScope<NavKey>.themeSettingScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.ThemeSetting> {
        ThemeSettingRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
        )
    }
}

fun EntryProviderScope<NavKey>.webViewScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.WebView> { key ->
        WebViewRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            viewModel =
                hiltViewModel<WebViewViewModel, WebViewViewModel.Factory>(
                    key = key.webUrl,
                ) { factory ->
                    factory.create(key)
                },
        )
    }
}
