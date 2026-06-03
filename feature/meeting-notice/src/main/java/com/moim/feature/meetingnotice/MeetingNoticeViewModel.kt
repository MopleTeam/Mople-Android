package com.moim.feature.meetingnotice

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.notice.NoticeRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = MeetingNoticeViewModel.Factory::class)
class MeetingNoticeViewModel @AssistedInject constructor(
    userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    private val noticeRepository: NoticeRepository,
    @Assisted val meetingNoticeRoute: DetailRoute.MeetingNotice,
) : BaseViewModel() {
    private var pagingJob: Job? = null
    private val meetingId = meetingNoticeRoute.meetId

    init {
        viewModelScope.launch {
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
    }

    fun onUiAction(uiAction: MeetingNoticeUiAction) {
        when (uiAction) {
            is MeetingNoticeUiAction.OnClickBack -> {
                setUiEvent(MeetingNoticeUiEvent.NavigateToBack)
            }

            is MeetingNoticeUiAction.OnClickRefresh -> {
                val current = uiState.value as? MeetingNoticeUiState ?: return
                getNotices(tabIndex = current.selectedTabIndex)
            }

            is MeetingNoticeUiAction.OnLoadNextPage -> {
                val current = uiState.value as? MeetingNoticeUiState ?: return
                getNotices(
                    tabIndex = current.selectedTabIndex,
                    cursor = current.pagingInfo.nextCursor,
                )
            }

            is MeetingNoticeUiAction.OnTabSelected -> {
                val current = uiState.value as? MeetingNoticeUiState ?: return
                if (current.selectedTabIndex == uiAction.tabIndex) return
                pagingJob?.cancel()
                setUiState(current.copy(selectedTabIndex = uiAction.tabIndex))
                getNotices(tabIndex = uiAction.tabIndex)
            }

            is MeetingNoticeUiAction.OnClickNotice -> {

            }
        }
    }

    private fun getNotices(
        tabIndex: Int,
        cursor: String? = null,
    ) {
        if (pagingJob.isActiveCheck()) return
        pagingJob =
            viewModelScope.launch {
                handlePagingData(
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
                    pagingInfo = pagingInfo,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private fun handlePagingData(
        pagingInfo: PaginationContainer<List<Notice>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        uiState.checkState<MeetingNoticeUiState> {
            val result =
                PagingHelper.handlePagingResult(
                    pagingData = pagingInfo,
                    isLoading = isLoading,
                    currentPagingInfo = this.pagingInfo,
                    currentItems = notices,
                    isInitialLoad = cursor == null,
                    transform = { it },
                )

            setUiState(
                copy(
                    pagingInfo = result.pagingInfo,
                    notices = result.items,
                ),
            )
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
    val notices: List<Notice> = emptyList(),
    val pagingInfo: PagingUiState = PagingUiState(),
) : UiState

sealed interface MeetingNoticeUiAction : UiAction {
    data object OnClickBack : MeetingNoticeUiAction

    data object OnClickRefresh : MeetingNoticeUiAction

    data class OnClickNotice(
        val notice: Notice,
    ) : MeetingNoticeUiAction

    data object OnLoadNextPage : MeetingNoticeUiAction

    data class OnTabSelected(
        val tabIndex: Int,
    ) : MeetingNoticeUiAction
}

sealed interface MeetingNoticeUiEvent : UiEvent {
    data object NavigateToBack : MeetingNoticeUiEvent
}
