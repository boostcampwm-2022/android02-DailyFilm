package com.boostcamp.dailyfilm.presentation.selectvideo


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.model.VideoItem
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.selectvideo.GalleryVideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class SelectVideoViewModel @Inject constructor(
    private val selectVideoRepository: GalleryVideoRepository
) : ViewModel(), VideoSelectListener {

    private val _videosState =
        MutableStateFlow(Result.Success<PagingData<VideoItem>>(PagingData.empty()))
    val videosState: StateFlow<Result<*>> get() = _videosState

    override val selectedVideo = MutableLiveData<VideoItem>()

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
