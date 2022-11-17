package com.boostcamp.dailyfilm.presentation.selectvideo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.boostcamp.dailyfilm.data.selectvideo.GalleryVideoRepository
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectVideoViewModel @Inject constructor(
    private val selectVideoRepository: GalleryVideoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), VideoSelectListener {

    val dateModel = savedStateHandle.get<DateModel>(CalendarActivity.KEY_DATE_MODEL)

    private val _videosState =
        MutableStateFlow(Result.Success<PagingData<VideoItem>>(PagingData.empty()))
    val videosState: StateFlow<Result<*>> get() = _videosState

    private val _uploadResult = MutableSharedFlow<Boolean>()
    val uploadResult : SharedFlow<Boolean> get() = _uploadResult

    override val selectedVideo = MutableLiveData<VideoItem>()

    init {
        Log.d("SelectVideoViewModel", "dateModel: $dateModel")
    }


    fun uploadVideo() {
        selectedVideo.value?.let {videoItem->
            selectVideoRepository.uploadVideo(videoItem).onEach { _uploadResult.emit(it)}.launchIn(viewModelScope)
        }
    }

    fun loadVideo() {
        viewModelScope.launch {
            selectVideoRepository.loadVideo().collect { videoItem ->
                _videosState.value = Result.Success(videoItem)
            }
        }
    }

    override fun chooseVideo(videoItem: VideoItem) {
        this.selectedVideo.value = videoItem
    }
}
