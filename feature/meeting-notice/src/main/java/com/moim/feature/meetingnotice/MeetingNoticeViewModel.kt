package com.moim.feature.meetingnotice

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import com.moim.core.common.model.PaginationContainer
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.notice.NoticeRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.NoticeAction
import com.moim.core.ui.eventbus.actionStateIn
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.meetingnotice.model.MeetingNoticeIntent
import com.moim.feature.meetingnotice.model.MeetingNoticeSideEffect
import com.moim.feature.meetingnotice.model.MeetingNoticeState
import com.moim.feature.meetingnotice.model.NoticeTabState
import com.moim.feature.meetingnotice.model.NoticeUiModel
import com.moim.feature.meetingnotice.model.asUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException

@HiltViewModel(assistedFactory = MeetingNoticeViewModel.Factory::class)
class MeetingNoticeViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    private val noticeRepository: NoticeRepository,
    @Assisted val meetingNoticeRoute: DetailRoute.MeetingNotice,
    noticeEventBus: EventBus<NoticeAction>,
) : MVIViewModel<MeetingNoticeState, MeetingNoticeSideEffect>(MeetingNoticeState()) {
    private val pagingJobs = mutableMapOf<Int, Job?>()
    private val meetingId = meetingNoticeRoute.meetId

    private val noticeActionReceiver =
        noticeEventBus
            .action
            .actionStateIn(viewModelScope, NoticeAction.None)

    init {
        noticeActionReceiver
            .onEach { action ->
                intent {
                    when (action) {
                        is NoticeAction.NoticeCreate -> applyNoticeCreate(action.notice)
                        is NoticeAction.NoticeUpdate -> applyNoticeUpdate(action.notice)
                        is NoticeAction.NoticeDelete -> applyNoticeDelete(action.noticeId)
                        is NoticeAction.None -> Unit
                    }
                }
            }.launchIn(viewModelScope)
    }

    override suspend fun Syntax<MeetingNoticeState, MeetingNoticeSideEffect>.onContainerCreate() {
        val user = userRepository.getUser().first()
        val meeting = runCatching { meetingRepository.getMeeting(meetingId) }.getOrNull()

        reduce {
            state.copy(
                user = user,
                isHostUser = meeting?.hostId == user.userId,
            )
        }

        getNotices(tabIndex = 0)
    }

    override fun onIntent(intent: Intent) {
        if (intent !is MeetingNoticeIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is MeetingNoticeIntent.BackClick -> {
                    postSideEffect(MeetingNoticeSideEffect.NavigateToBack)
                }

                is MeetingNoticeIntent.WriteClick -> {
                    postSideEffect(MeetingNoticeSideEffect.NavigateToMeetingNoticeWrite(meetingId))
                }

                is MeetingNoticeIntent.RefreshClick -> {
                    getNotices(tabIndex = state.selectedTabIndex)
                }

                is MeetingNoticeIntent.NextPageLoad -> {
                    getNotices(
                        tabIndex = state.selectedTabIndex,
                        cursor = state.currentTab.pagingInfo.nextCursor,
                    )
                }

                is MeetingNoticeIntent.TabSelect -> {
                    if (state.selectedTabIndex == intent.tabIndex) return@intent
                    reduce { state.copy(selectedTabIndex = intent.tabIndex) }

                    // 이미 불러온 탭이면 캐시를 그대로 사용하고, 처음 보는 탭만 새로 불러온다.
                    val tab = state.tabStates[intent.tabIndex]
                    if (tab == null || !tab.isLoaded) {
                        getNotices(tabIndex = intent.tabIndex)
                    }
                }

                is MeetingNoticeIntent.NoticeClick -> {
                    postSideEffect(
                        MeetingNoticeSideEffect.NavigateToMeetingNoticeDetail(
                            meetId = intent.notice.meetId,
                            noticeId = intent.notice.noticeId,
                        ),
                    )
                }

                is MeetingNoticeIntent.PinClick -> {
                    setPinNotice(
                        noticeId = intent.notice.noticeId,
                        isPin = !intent.notice.pinned,
                    )
                }
            }
        }
    }

    private fun getNotices(
        tabIndex: Int,
        cursor: String? = null,
    ) {
        if (pagingJobs[tabIndex].isActiveCheck()) return
        pagingJobs[tabIndex] =
            intent {
                handlePagingData(
                    tabIndex = tabIndex,
                    pagingData = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingData =
                    runCatching {
                        noticeRepository.getNotices(
                            meetId = meetingId,
                            cursor = cursor ?: "",
                            size = 30,
                            filterType = filterTypeOf(tabIndex),
                        )
                    }.getOrNull()

                if (!currentCoroutineContext().isActive) return@intent

                handlePagingData(
                    tabIndex = tabIndex,
                    pagingData = pagingData,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private suspend fun Syntax<MeetingNoticeState, MeetingNoticeSideEffect>.handlePagingData(
        tabIndex: Int,
        pagingData: PaginationContainer<List<Notice>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        val tab = state.tabStates[tabIndex] ?: NoticeTabState()
        val result =
            PagingHelper.handlePagingResult(
                pagingData = pagingData,
                isLoading = isLoading,
                currentPagingInfo = tab.pagingInfo,
                currentItems = tab.notices,
                isInitialLoad = cursor == null,
                transform = { notices -> notices.map { it.asUiModel() } },
            )

        val updatedTab =
            tab.copy(
                pagingInfo = result.pagingInfo,
                notices = result.items,
                // 로드가 성공적으로 끝난 시점에 캐시됨으로 표시
                isLoaded = tab.isLoaded || (!isLoading && pagingData != null),
            )

        reduce { state.copy(tabStates = state.tabStates + (tabIndex to updatedTab)) }
    }

    private suspend fun Syntax<MeetingNoticeState, MeetingNoticeSideEffect>.applyNoticeCreate(notice: Notice) {
        if (notice.meetId != meetingId) return

        val uiModel = notice.asUiModel()
        // 로드된 탭들 중 필터 조건에 맞는 탭에만 새 공지를 삽입
        val updated =
            state.tabStates.mapValues { (tabIndex, tab) ->
                if (!tab.isLoaded) return@mapValues tab
                if (tab.notices.any { it.noticeId == uiModel.noticeId }) return@mapValues tab

                val filter = filterTypeOf(tabIndex)
                if (filter != null && filter != uiModel.type) return@mapValues tab

                // 고정 공지 묶음 아래에 새 공지를 삽입
                val insertIndex = tab.notices.indexOfLast { it.pinned } + 1
                tab.copy(
                    notices = tab.notices.toMutableList().apply { add(insertIndex, uiModel) },
                )
            }

        reduce { state.copy(tabStates = updated) }
    }

    private suspend fun Syntax<MeetingNoticeState, MeetingNoticeSideEffect>.applyNoticeUpdate(notice: Notice) {
        val uiModel = notice.asUiModel()
        val updated =
            state.tabStates.mapValues { (_, tab) ->
                if (tab.notices.none { it.noticeId == uiModel.noticeId }) {
                    tab
                } else {
                    tab.copy(notices = tab.notices.map { if (it.noticeId == uiModel.noticeId) uiModel else it })
                }
            }

        reduce { state.copy(tabStates = updated) }
    }

    private suspend fun Syntax<MeetingNoticeState, MeetingNoticeSideEffect>.applyNoticeDelete(noticeId: String) {
        val updated =
            state.tabStates.mapValues { (_, tab) ->
                val filtered = tab.notices.filterNot { it.noticeId == noticeId }
                if (filtered.size == tab.notices.size) tab else tab.copy(notices = filtered)
            }

        reduce { state.copy(tabStates = updated) }
    }

    private suspend fun Syntax<MeetingNoticeState, MeetingNoticeSideEffect>.setPinNotice(
        noticeId: String,
        isPin: Boolean,
    ) {
        setLoading(true)

        try {
            val notice =
                if (isPin) {
                    noticeRepository.pinNotice(noticeId = noticeId)
                } else {
                    noticeRepository.unpinNotice(noticeId = noticeId)
                }

            applyNoticePinned(notice)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showErrorToast(e)
        } finally {
            setLoading(false)
        }
    }

    private suspend fun Syntax<MeetingNoticeState, MeetingNoticeSideEffect>.applyNoticePinned(notice: Notice) {
        val uiModel = notice.asUiModel()
        val updated =
            state.tabStates.mapValues { (_, tab) ->
                if (tab.notices.none { it.noticeId == uiModel.noticeId }) {
                    tab
                } else {
                    // 고정 상태를 반영한 뒤 고정 공지가 항상 위로 오도록 재정렬한다.
                    val reordered =
                        tab.notices
                            .map { if (it.noticeId == uiModel.noticeId) uiModel else it }
                            .sortedWith(
                                compareByDescending<NoticeUiModel> { it.pinned }
                                    .thenByDescending { it.createdAt },
                            )
                    tab.copy(notices = reordered)
                }
            }

        reduce { state.copy(tabStates = updated) }
    }

    private suspend fun Syntax<MeetingNoticeState, MeetingNoticeSideEffect>.showErrorToast(exception: Throwable) {
        val message = if (exception is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
        postSideEffect(MeetingNoticeSideEffect.ShowToastMessage(message))
    }

    private fun filterTypeOf(tabIndex: Int): NoticeType? =
        when (tabIndex) {
            TAB_INDEX_CUSTOM -> NoticeType.CUSTOM
            TAB_INDEX_SYSTEM -> NoticeType.SYSTEM
            else -> null
        }

    @AssistedFactory
    interface Factory {
        fun create(meetingDetailRoute: DetailRoute.MeetingNotice): MeetingNoticeViewModel
    }

    companion object {
        private const val TAB_INDEX_CUSTOM = 1
        private const val TAB_INDEX_SYSTEM = 2
    }
}

const val MEETING_NOTICE_TAB_COUNT = 3
