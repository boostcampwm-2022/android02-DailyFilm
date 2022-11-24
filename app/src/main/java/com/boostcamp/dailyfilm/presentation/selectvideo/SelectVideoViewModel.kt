package com.boostcamp.dailyfilm.presentation.selectvideo

import androidx.lifecycle.*
import androidx.paging.PagingData
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.boostcamp.dailyfilm.data.selectvideo.GalleryVideoRepository
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectVideoViewModel @Inject constructor(
    private val selectVideoRepository: GalleryVideoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), VideoSelectListener {

    val dateModel = savedStateHandle.get<DateModel>(CalendarActivity.KEY_DATE_MODEL)
    override val viewTreeLifecycleScope: CoroutineScope
        get() = viewModelScope

    private val _videosState =
        MutableStateFlow(Result.Success<PagingData<VideoItem>>(PagingData.empty()))
    val videosState: StateFlow<Result<*>> get() = _videosState

    private val _selectedVideo = MutableStateFlow<VideoItem?>(null)
    override val selectedVideo = _selectedVideo.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SelectVideoEvent>()
    val eventFlow: SharedFlow<SelectVideoEvent> = _eventFlow.asSharedFlow()

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

    fun loadVideo() {
        viewModelScope.launch {
            selectVideoRepository.loadVideo().collect { videoItem ->
                _videosState.value = Result.Success(videoItem)
            }
        }
    }

    private fun event(event: SelectVideoEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    override fun chooseVideo(videoItem: VideoItem?) {
        viewModelScope.launch {
            _selectedVideo.emit(videoItem)
        }
    }

}

sealed class SelectVideoEvent {
    data class NextButtonResult(val dateAndVideoModelItem: DateAndVideoModel) : SelectVideoEvent()
}

