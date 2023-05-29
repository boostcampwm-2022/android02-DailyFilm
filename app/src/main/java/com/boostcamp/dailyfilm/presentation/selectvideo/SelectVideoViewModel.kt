package com.boostcamp.dailyfilm.presentation.selectvideo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.boostcamp.dailyfilm.data.selectvideo.GalleryVideoRepository
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_EDIT_STATE
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.model.EditState
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectVideoViewModel @Inject constructor(
    private val selectVideoRepository: GalleryVideoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val dateModel = savedStateHandle.get<DateModel>(CalendarActivity.KEY_DATE_MODEL)
    val calendarIndex = savedStateHandle.get<Int>(DateFragment.KEY_CALENDAR_INDEX)
    val editState = savedStateHandle.get<EditState>(KEY_EDIT_STATE)

    private val _selectedVideo = MutableStateFlow<VideoItem?>(null)
    val selectedVideo = _selectedVideo.asStateFlow()

    private var clickSound = false

    private val _eventFlow = MutableSharedFlow<SelectVideoEvent>()
    val eventFlow: SharedFlow<SelectVideoEvent> = _eventFlow.asSharedFlow()

    private val _videoItems = MutableStateFlow<PagingData<VideoItem>>(PagingData.empty())
    val videoItems: StateFlow<PagingData<VideoItem>> get() = _videoItems

    fun navigateToUpload() {
        viewModelScope.launch {
            selectedVideo.value?.let { selectedVideoItem ->
                if (dateModel != null) {
                    event(
                        SelectVideoEvent.NextButtonResult(
                            DateAndVideoModel(
                                selectedVideoItem.uri,
                                dateModel.getDate()
                            )
                        )
                    )
                }
            }
        }
    }

    fun controlSound() {
        clickSound = !clickSound
        event(SelectVideoEvent.ControlSoundResult(clickSound))
    }

    fun backToMain() {
        event(SelectVideoEvent.BackButtonResult(true))
    }

    fun loadVideo() {
        selectVideoRepository.loadVideo().cachedIn(viewModelScope).onEach { pagingData ->
            _videoItems.value = pagingData
        }.launchIn(viewModelScope)
    }

    private fun event(event: SelectVideoEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    fun chooseVideo(videoItem: VideoItem?) {
        viewModelScope.launch {
            _selectedVideo.emit(videoItem)
        }
    }

}

sealed class SelectVideoEvent {
    data class NextButtonResult(val dateAndVideoModelItem: DateAndVideoModel) : SelectVideoEvent()
    data class BackButtonResult(val result: Boolean) : SelectVideoEvent()
    data class ControlSoundResult(val result: Boolean) : SelectVideoEvent()
}

