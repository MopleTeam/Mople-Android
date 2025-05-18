package com.moim.feature.main

import androidx.lifecycle.viewModelScope
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.view.BaseViewModel
import com.moim.core.data.datasource.meeting.MeetingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
    meetingViewModelDelegate: MeetingViewModelDelegate,
) : BaseViewModel(), MeetingViewModelDelegate by meetingViewModelDelegate {

    private val _meetingId = MutableStateFlow<String?>(null)
    val meetingId = _meetingId.asStateFlow()

    private val _planId = MutableStateFlow<String?>(null)
    val planId = _planId.asStateFlow()

    private val _reviewId = MutableStateFlow<String?>(null)
    val reviewId = _reviewId.asStateFlow()

    fun setPlanId(planId: String) {
        _planId.update { planId }
    }

    fun setReviewId(reviewId: String) {
        _reviewId.update { reviewId }
    }

    fun setMeetingId(meetingId: String) {
        _meetingId.update { meetingId }
    }

    fun setJoinMeeting(meetCode: String) {
        viewModelScope.launch {
            runCatching {
                val meeting = meetingRepository.joinMeeting(meetCode).first()
                invalidateMeeting(ZonedDateTime.now())
                _meetingId.update { meeting.id }
            }
        }
    }
}