package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmRepository
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    val beforeItem = savedStateHandle.get<DateAndVideoModel>("beforeItem")
    private val _uploadResult = MutableSharedFlow<Uri?>()
    val uploadResult: SharedFlow<Uri?> get() = _uploadResult

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Uninitialized)
    val uiState = _uiState.asStateFlow()

    val textContent = MutableLiveData("")

    private val _cancelUploadResult = MutableSharedFlow<Boolean>()
    val cancelUploadResult: SharedFlow<Boolean> get() = _cancelUploadResult

    private val _clickSound = MutableStateFlow(true)
    val clickSound = _clickSound.asStateFlow()

    fun uploadVideo() {
        val text = textContent.value ?: ""
        if (text.isEmpty()) {
            _uiState.value = UiState.Failure(Throwable("영상에 맞는 문구를 입력해주세요."))
            return
        }
        infoItem?.let { item ->
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                // _uploadFilmInfoResult.emit(false)
                uploadFilmRepository.uploadVideo(item.uri).collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            // storage 업로드 성공
                            //_uploadResult.emit(result.data)
                            uploadFilmInfo(result.data)
                        }
                        is Result.Error -> {
                            // storage 업로드 실패
                            _uiState.value = UiState.Failure(result.exception)
                        }
                        is Result.Uninitialized -> {
                        }
                    }
                }
            }
        }
    }

    private fun uploadFilmInfo(videoUrl: Uri?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val uploadDate = infoItem?.uploadDate
        val text = textContent.value ?: ""

        if (userId != null && videoUrl != null && uploadDate != null) {
            uploadFilmRepository.uploadFilmInfo(
                userId,
                uploadDate,
                DailyFilmItem(videoUrl.toString(), text, uploadDate)
            )
                .onEach {
                    when (it) {
                        is Result.Success -> {
                            _uiState.value = UiState.Success(it.data)
                        }
                        is Result.Error -> {
                            _uiState.value = UiState.Failure(it.exception)
                        }
                        is Result.Uninitialized -> {

                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _uiState.value =
                UiState.Failure(Throwable("userId == null or videoUrl == null or uploadDate or null "))
        }
    }

    fun controlSound() {
        _clickSound.value = !_clickSound.value
    }

    fun cancelUploadVideo() {
        viewModelScope.launch {
            _cancelUploadResult.emit(true)
        }
    }
}