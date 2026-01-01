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
import com.moim.feature.plandetail.PlanDetailRoute
import com.moim.feature.plandetail.PlanDetailViewModel
import com.moim.feature.planwrite.PlanWriteRoute
import com.moim.feature.planwrite.PlanWriteViewModel
import com.moim.feature.profile.ProfileRoute
import com.moim.feature.profileupdate.ProfileUpdateRoute
import com.moim.feature.reviewwrite.ReviewWriteRoute
import com.moim.feature.reviewwrite.ReviewWriteViewModel
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
            navigateToMeetingWrite = { navigator.navigateToMeetingWrite() },
            navigateToPlanWrite = { navigator.navigateToPlanWrite() },
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
            navigateToMeetingWrite = { navigator.navigateToMeetingWrite() },
            navigateToMeetingDetail = navigator::navigateToMeetingDetail
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
            navigateToPlanDetail = navigator::navigateToPlanDetail
        )
    }
}

fun EntryProviderScope<NavKey>.profileScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
    navigateToIntro: () -> Unit
) {
    entry<MainRoute.Profile> {
        ProfileRoute(
            padding = paddingValues,
            navigateToProfileUpdate = navigator::navigateToProfileUpdate,
            navigateToAlarmSetting = navigator::navigateToAlarmSetting,
            navigateToPrivacyPolicy = navigator::navigateToWebView,
            navigateToIntro = navigateToIntro
        )
    }
}

// === Detail Screens ===
fun EntryProviderScope<NavKey>.meetingDetailScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.MeetingDetail> { key ->
        val meetingId = key.meetingId

        MeetingDetailRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            navigateToPlanWrite = { navigator.navigateToPlanWrite() },
            navigateToPlanDetail = navigator::navigateToPlanDetail,
            navigateToMeetingSetting = { meeting ->
                navigator.navigateToMeetingSetting(meeting)
            },
            navigateToImageViewer = { title, images, position, defaultImage ->
                navigator.navigateToImageViewer(title, images, position, defaultImage)
            },
            viewModel = hiltViewModel<MeetingDetailViewModel, MeetingDetailViewModel.Factory>(
                key = meetingId,
            ) { factory ->
                factory.create(meetingId)
            },
        )
    }
}

fun EntryProviderScope<NavKey>.meetingWriteScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.MeetingWrite> { key ->
        val meeting = key.meeting
        MeetingWriteRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            viewModel = hiltViewModel<MeetingWriteViewModel, MeetingWriteViewModel.Factory>(
                key = meeting?.id,
            ) { factory ->
                factory.create(meeting)
            },
        )
    }
}

fun EntryProviderScope<NavKey>.meetingSettingScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.MeetingSetting> { key ->
        val meeting = key.meeting
        MeetingSettingRoute(
            padding = paddingValues,
            navigateToBack = { shouldPopTwice ->
                navigator.goBack()
                if (shouldPopTwice) navigator.goBack()
            },
            navigateToParticipants = { viewIdType ->
                navigator.navigateToParticipantList(viewIdType)
            },
            navigateToMeetingWrite = { meeting ->
                navigator.navigateToMeetingWrite(meeting)
            },
            viewModel = hiltViewModel<MeetingSettingViewModel, MeetingSettingViewModel.Factory>(
                key = meeting.id,
            ) { factory ->
                factory.create(meeting)
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
            viewModel = hiltViewModel<MapDetailViewModel, MapDetailViewModel.Factory>(
                key = "${key.latitude}_${key.longitude}",
            ) { factory ->
                factory.create(
                    mapDetail = key,
                )
            },
        )
    }
}

fun EntryProviderScope<NavKey>.planDetailScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.PlanDetail> { key ->
        val viewIdType = key.viewIdType

        PlanDetailRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            navigateToMapDetail = { placeName, address, lat, lng ->
                navigator.navigateToMapDetail(placeName, address, lat, lng)
            },
            navigateToParticipants = navigator::navigateToParticipantList,
            navigateToPlanWrite = { planItem ->
                navigator.navigateToPlanWrite(planItem)
            },
            navigateToReviewWrite = { postId, isUpdated ->
                navigator.navigateToReviewWrite(postId, isUpdated)
            },
            navigateToCommentDetail = { meetId, postId, comment ->
                navigator.navigateToCommentDetail(meetId, postId, comment)
            },
            navigateToImageViewer = { title, images, position, defaultImage ->
                navigator.navigateToImageViewer(title, images, position, defaultImage)
            },
            viewModel = hiltViewModel<PlanDetailViewModel, PlanDetailViewModel.Factory>(
                key = viewIdType.id,
            ) { factory ->
                factory.create(viewIdType)
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
            navigateToImageViewer = { title, images, position, defaultImage ->
                navigator.navigateToImageViewer(title, images, position, defaultImage)
            },
            viewModel = hiltViewModel<CommentDetailViewModel, CommentDetailViewModel.Factory>(
                key = key.postId,
            ) { factory ->
                factory.create(
                    commentDetail = key
                )
            },
        )
    }
}

fun EntryProviderScope<NavKey>.planWriteScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.PlanWrite> { key ->
        val planItem = key.planItem

        PlanWriteRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            viewModel = hiltViewModel<PlanWriteViewModel, PlanWriteViewModel.Factory>(
                key = planItem?.postId,
            ) { factory ->
                factory.create(planItem)
            },
        )
    }
}

fun EntryProviderScope<NavKey>.reviewWriteScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.ReviewWrite> { key ->
        val postId = key.postId
        val isUpdated = key.isUpdated
        ReviewWriteRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            navigateToParticipants = navigator::navigateToParticipantList,
            viewModel = hiltViewModel<ReviewWriteViewModel, ReviewWriteViewModel.Factory>(
                key = postId
            ) { factory ->
                factory.create(postId, isUpdated)
            }
        )
    }
}

fun EntryProviderScope<NavKey>.participantListScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.ParticipantList> { key ->
        val viewIdType = key.viewIdType

        ParticipantListRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            navigateToImageViewer = { title, images, position, defaultImage ->
                navigator.navigateToImageViewer(title, images, position, defaultImage)
            },
            viewModel = hiltViewModel<ParticipantListViewModel, ParticipantListViewModel.Factory>(
                key = viewIdType.id
            ) { factory ->
                factory.create(viewIdType)
            }
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
            viewModel = hiltViewModel<ImageViewerViewModel, ImageViewerViewModel.Factory>(
                key = key.title
            ) { factory ->
                factory.create(key)
            }
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
            navigateToBack = navigator::goBack
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
            navigateToBack = navigator::goBack
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
            navigateToBack = navigator::goBack
        )
    }
}

fun EntryProviderScope<NavKey>.webViewScreenEntry(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
) {
    entry<DetailRoute.WebView> { key ->
        val webUrl = key.webUrl
        WebViewRoute(
            padding = paddingValues,
            navigateToBack = navigator::goBack,
            viewModel = hiltViewModel<WebViewViewModel, WebViewViewModel.Factory>(
                key = webUrl,
            ) { factory ->
                factory.create(webUrl)
            },
        )
    }
}