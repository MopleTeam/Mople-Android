package com.moim.feature.meetingnotice

import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.notice.NoticeRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.NoticeAction
import com.moim.core.ui.eventbus.actionStateIn
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.feature.meetingnotice.model.NoticeUiModel
import com.moim.feature.meetingnotice.model.asUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel(assistedFactory = MeetingNoticeViewModel.Factory::class)
class MeetingNoticeViewModel @AssistedInject constructor(
    userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    private val noticeRepository: NoticeRepository,
    @Assisted val meetingNoticeRoute: DetailRoute.MeetingNotice,
    noticeEventBus: EventBus<NoticeAction>,
) : BaseViewModel() {
    private val pagingJobs = mutableMapOf<Int, Job?>()
    private val meetingId = meetingNoticeRoute.meetId

    private val noticeActionReceiver =
        noticeEventBus
            .action
            .actionStateIn(viewModelScope, NoticeAction.None)

    init {
        viewModelScope.launch {
            launch {
                val user = userRepository.getUser().first()
                val meeting = runCatching { meetingRepository.getMeeting(meetingId).first() }.getOrNull()

                setUiState(
                    MeetingNoticeUiState(
                        user = user,
                        isHostUser = meeting?.hostId == user.userId,
                    ),
                )
                getNotices(tabIndex = 0)
            }

            launch {
                noticeActionReceiver.collect { action ->
                    when (action) {
                        is NoticeAction.NoticeCreate -> applyNoticeCreate(action.notice)
                        is NoticeAction.NoticeUpdate -> applyNoticeUpdate(action.notice)
                        is NoticeAction.NoticeDelete -> applyNoticeDelete(action.noticeId)
                        is NoticeAction.None -> Unit
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: MeetingNoticeUiAction) {
        when (uiAction) {
            is MeetingNoticeUiAction.OnClickBack -> {
                setUiEvent(MeetingNoticeUiEvent.NavigateToBack)
            }

            is MeetingNoticeUiAction.OnClickWrite -> {
                setUiEvent(MeetingNoticeUiEvent.NavigateToMeetingNoticeWrite(meetingId))
            }

            is MeetingNoticeUiAction.OnClickRefresh -> {
                val current = uiState.value as? MeetingNoticeUiState ?: return
                getNotices(tabIndex = current.selectedTabIndex)
            }

            is MeetingNoticeUiAction.OnLoadNextPage -> {
                val current = uiState.value as? MeetingNoticeUiState ?: return
                getNotices(
                    tabIndex = current.selectedTabIndex,
                    cursor = current.currentTab.pagingInfo.nextCursor,
                )
            }

            is MeetingNoticeUiAction.OnTabSelected -> {
                val current = uiState.value as? MeetingNoticeUiState ?: return
                if (current.selectedTabIndex == uiAction.tabIndex) return
                setUiState(current.copy(selectedTabIndex = uiAction.tabIndex))

                // 이미 불러온 탭이면 캐시를 그대로 사용하고, 처음 보는 탭만 새로 불러온다.
                val tab = current.tabStates[uiAction.tabIndex]
                if (tab == null || !tab.isLoaded) {
                    getNotices(tabIndex = uiAction.tabIndex)
                }
            }

            is MeetingNoticeUiAction.OnClickNotice -> {
                setUiEvent(
                    MeetingNoticeUiEvent.NavigateToMeetingNoticeDetail(
                        meetId = uiAction.notice.meetId,
                        noticeId = uiAction.notice.noticeId,
                    ),
                )
            }

            is MeetingNoticeUiAction.OnClickPin -> {
                setPinNotice(
                    noticeId = uiAction.notice.noticeId,
                    isPin = !uiAction.notice.pinned,
                )
            }
        }
    }

    private fun getNotices(
        tabIndex: Int,
        cursor: String? = null,
    ) {
        if (pagingJobs[tabIndex].isActiveCheck()) return
        pagingJobs[tabIndex] =
            viewModelScope.launch {
                handlePagingData(
                    tabIndex = tabIndex,
                    pagingInfo = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingInfo =
                    runCatching {
                        noticeRepository.getNotices(
                            meetId = meetingId,
                            cursor = cursor ?: "",
                            size = 30,
                            filterType = filterTypeOf(tabIndex),
                        )
                    }.getOrNull()

                if (!isActive) return@launch

                handlePagingData(
                    tabIndex = tabIndex,
                    pagingInfo = pagingInfo,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private fun handlePagingData(
        tabIndex: Int,
        pagingInfo: PaginationContainer<List<Notice>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        uiState.checkState<MeetingNoticeUiState> {
            val tab = tabStates[tabIndex] ?: NoticeTabState()
            val result =
                PagingHelper.handlePagingResult(
                    pagingData = pagingInfo,
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
                    isLoaded = tab.isLoaded || (!isLoading && pagingInfo != null),
                )

            setUiState(copy(tabStates = tabStates + (tabIndex to updatedTab)))
        }
    }

    private fun applyNoticeCreate(notice: Notice) {
        uiState.checkState<MeetingNoticeUiState> {
            if (notice.meetId != meetingId) return@checkState

            val uiModel = notice.asUiModel()
            // 로드된 탭들 중 필터 조건에 맞는 탭에만 새 공지를 삽입
            val updated =
                tabStates.mapValues { (tabIndex, tab) ->
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
            setUiState(copy(tabStates = updated))
        }
    }

    private fun applyNoticeUpdate(notice: Notice) {
        uiState.checkState<MeetingNoticeUiState> {
            val uiModel = notice.asUiModel()
            val updated =
                tabStates.mapValues { (_, tab) ->
                    if (tab.notices.none { it.noticeId == uiModel.noticeId }) {
                        tab
                    } else {
                        tab.copy(notices = tab.notices.map { if (it.noticeId == uiModel.noticeId) uiModel else it })
                    }
                }
            setUiState(copy(tabStates = updated))
        }
    }

    private fun applyNoticeDelete(noticeId: String) {
        uiState.checkState<MeetingNoticeUiState> {
            val updated =
                tabStates.mapValues { (_, tab) ->
                    val filtered = tab.notices.filterNot { it.noticeId == noticeId }
                    if (filtered.size == tab.notices.size) tab else tab.copy(notices = filtered)
                }
            setUiState(copy(tabStates = updated))
        }
    }

    private fun setPinNotice(
        noticeId: String,
        isPin: Boolean,
    ) {
        viewModelScope.launch {
            if (isPin) {
                noticeRepository.pinNotice(noticeId = noticeId)
            } else {
                noticeRepository.unpinNotice(noticeId = noticeId)
            }.asResult()
                .onEach { setLoading(it is Result.Loading) }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            applyNoticePinned(result.data)
                        }

                        is Result.Error -> {
                            when (result.exception) {
                                is IOException -> setUiEvent(MeetingNoticeUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                is NetworkException -> setUiEvent(MeetingNoticeUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                            }
                        }
                    }
                }
        }
    }

    private fun applyNoticePinned(notice: Notice) {
        uiState.checkState<MeetingNoticeUiState> {
            val uiModel = notice.asUiModel()
            val updated =
                tabStates.mapValues { (_, tab) ->
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
            setUiState(copy(tabStates = updated))
        }
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

data class MeetingNoticeUiState(
    val user: User = User(userId = ""),
    val isHostUser: Boolean = false,
    val selectedTabIndex: Int = 0,
    val tabStates: Map<Int, NoticeTabState> = emptyMap(),
) : UiState {
    val currentTab: NoticeTabState
        get() = tabStates[selectedTabIndex] ?: NoticeTabState()
}

data class NoticeTabState(
    val notices: List<NoticeUiModel> = emptyList(),
    val pagingInfo: PagingUiState = PagingUiState(),
    val isLoaded: Boolean = false,
)

sealed interface MeetingNoticeUiAction : UiAction {
    data object OnClickBack : MeetingNoticeUiAction

    data object OnClickWrite : MeetingNoticeUiAction

    data object OnClickRefresh : MeetingNoticeUiAction

    data class OnClickNotice(
        val notice: NoticeUiModel,
    ) : MeetingNoticeUiAction

    data class OnClickPin(
        val notice: NoticeUiModel,
    ) : MeetingNoticeUiAction

    data object OnLoadNextPage : MeetingNoticeUiAction

    data class OnTabSelected(
        val tabIndex: Int,
    ) : MeetingNoticeUiAction
}

sealed interface MeetingNoticeUiEvent : UiEvent {
    data object NavigateToBack : MeetingNoticeUiEvent

    data class NavigateToMeetingNoticeWrite(
        val meetId: String,
    ) : MeetingNoticeUiEvent

    data class NavigateToMeetingNoticeDetail(
        val meetId: String,
        val noticeId: String,
    ) : MeetingNoticeUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingNoticeUiEvent
}
