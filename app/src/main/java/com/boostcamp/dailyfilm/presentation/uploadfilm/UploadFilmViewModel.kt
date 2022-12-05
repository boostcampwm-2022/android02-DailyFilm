package com.boostcamp.dailyfilm.presentation.uploadfilm

import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import androidx.lifecycle.LiveData
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val _showedTextContent = MutableLiveData<SpannableString>()
    val showedTextContent: LiveData<SpannableString> get() = _showedTextContent

    private val _uploadFilmInfoResult = MutableSharedFlow<Boolean>()
    val uploadFilmInfoResult: SharedFlow<Boolean> get() = _uploadFilmInfoResult

    val textContent = MutableLiveData("")

    private val _cancelUploadResult = MutableSharedFlow<Boolean>()
    val cancelUploadResult: SharedFlow<Boolean> get() = _cancelUploadResult

    private val _isWriting = MutableLiveData(false)
    val isWriting: LiveData<Boolean> get() = _isWriting

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
                            // _uploadResult.emit(result.data)
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
            val filmItem = DailyFilmItem(videoUrl.toString(), text, uploadDate)
            uploadFilmRepository.uploadFilmInfo(
                userId,
                uploadDate,
                filmItem
            ).onEach {
                when (it) {
                    is Result.Success -> {
                        uploadFilmRepository.insertFilmEntity(filmItem)
                        _uiState.value = UiState.Success(it.data)
                    }
                    is Result.Error -> {
                        _uiState.value = UiState.Failure(it.exception)
                    }
                    is Result.Uninitialized -> {}
                }
            }.launchIn(viewModelScope)
        } else {
            _uiState.value =
                UiState.Failure(Throwable("userId == null or videoUrl == null or uploadDate or null "))
        }
    }

    fun updateSpannableText() {
        textContent.value?.let { text ->
            if (text.isNotEmpty()) {
                _showedTextContent.value = SpannableString(text).apply {
                    setSpan(
                        BackgroundColorSpan(Color.BLACK),
                        0,
                        text.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } else {
                _showedTextContent.value = SpannableString("")
            }
        }
    }

    fun changeIsWriting() {
        _isWriting.value?.let {
            _isWriting.value = it.not()
        }
    }

    fun updateIsWriting(flag: Boolean) {
        _isWriting.value = flag
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
