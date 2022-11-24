package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmRepository
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadFilmViewModel @Inject constructor(
    private val uploadFilmRepository: UploadFilmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val infoItem = savedStateHandle.get<DateAndVideoModel>(SelectVideoActivity.DATE_VIDEO_ITEM)

    private val _uploadResult = MutableSharedFlow<Uri?>()
    val uploadResult: SharedFlow<Uri?> get() = _uploadResult

    val textContent = MutableLiveData("")

    private val _uploadFilmInfoResult = MutableSharedFlow<Boolean>()
    val uploadFilmInfoResult: SharedFlow<Boolean> get() = _uploadFilmInfoResult

    private val _cancelUploadResult = MutableSharedFlow<Boolean>()
    val cancelUploadResult: SharedFlow<Boolean> get() = _cancelUploadResult

    init {
        viewModelScope.launch {
            uploadResult.collect { uri ->
                uploadFilmInfo(uri)
            }
        }
    }

    fun uploadVideo() {
        infoItem?.let { item ->
            viewModelScope.launch {
                _uploadFilmInfoResult.emit(false)
                uploadFilmRepository.uploadVideo(item.uri).collectLatest {
                    _uploadResult.emit(it)
                }
            }
        }
    }

    fun cancelUploadVideo() {
        viewModelScope.launch {
            _cancelUploadResult.emit(true)
        }
    }

    private fun uploadFilmInfo(videoUrl: Uri?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val uploadDate = infoItem?.uploadDate
        val text = textContent.value ?: ""

        if (userId != null && videoUrl != null && uploadDate != null && text.isNotEmpty()) {
            uploadFilmRepository.uploadFilmInfo(
                userId,
                uploadDate,
                DailyFilmItem(videoUrl.toString(), text, uploadDate)
            )
                .onEach { _uploadFilmInfoResult.emit(it) }.launchIn(viewModelScope)
        } else {
            // 만약 텍스트나 오류가 났을경우에 true값 emit 하여서 액티비티에서 정상처리되는데 알맞은 오류 처리 필요
            viewModelScope.launch {
                _uploadFilmInfoResult.emit(true)
            }
        }
    }
}

sealed class UploadFilmEvent {
    data class CompleteButtonResult(val uploaded: Boolean) : UploadFilmEvent()
}