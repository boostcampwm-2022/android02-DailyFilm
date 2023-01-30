package com.boostcamp.dailyfilm.presentation.uploadfilm


import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.boostcamp.dailyfilm.data.delete.DeleteFilmRepository
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmRepository
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_EDIT_FLAG
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR_INDEX
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmFragment.Companion.KEY_DATE_MODEL
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity.Companion.DATE_VIDEO_ITEM
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.boostcamp.dailyfilm.presentation.util.RoundedBackgroundSpan
import com.boostcamp.dailyfilm.presentation.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadFilmViewModel @Inject constructor(
    private val deleteFilmRepository: DeleteFilmRepository,
    private val uploadFilmRepository: UploadFilmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val infoItem = savedStateHandle.get<DateAndVideoModel>(DATE_VIDEO_ITEM)
    val beforeItem = savedStateHandle.get<DateAndVideoModel>(KEY_INFO_ITEM)
    val dateModel = savedStateHandle.get<DateModel>(KEY_DATE_MODEL)
    val calendarIndex = savedStateHandle.get<Int>(KEY_CALENDAR_INDEX)
    private val editFlag = savedStateHandle.get<Boolean>(KEY_EDIT_FLAG)

    private val _uploadResult = MutableSharedFlow<Uri?>()
    val uploadResult: SharedFlow<Uri?> get() = _uploadResult

    private val _uiState = MutableStateFlow<UiState<DateModel>>(UiState.Uninitialized)
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
        editFlag?.let { flag ->
            if (flag) {
                deleteVideo()
            } else {
                uploadStorage()
            }
        }
    }

    private fun uploadStorage() {
        infoItem?.let { item ->
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                // _uploadFilmInfoResult.emit(false)
                uploadFilmRepository.uploadVideo(item.uploadDate, item.uri)
                    .collectLatest { result ->
                        when (result) {
                            is Result.Success -> {
                                // storage 업로드 성공
                                // _uploadResult.emit(result.data)
                                uploadRealtime(result.data)
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

    private fun uploadRealtime(videoUrl: Uri?) {
        val uploadDate = infoItem?.uploadDate
        val text = textContent.value ?: ""

        dateModel ?: return
        if (videoUrl != null && uploadDate != null) {
            val filmItem = DailyFilmItem(videoUrl.toString(), text, uploadDate)
            uploadFilmRepository.uploadFilmInfo(
                uploadDate,
                filmItem
            ).onEach {
                when (it) {
                    is Result.Success -> {
                        uploadFilmRepository.insertFilmEntity(filmItem)
                        _uiState.value = UiState.Success(
                            DateModel(
                                year = dateModel.year,
                                month = dateModel.month,
                                day = dateModel.day,
                                text = text,
                                videoUrl = videoUrl.toString()
                            )
                        )
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
                        RoundedBackgroundSpan(),
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

    private fun deleteVideo() {
        dateModel ?: return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val updateDate = dateModel.getDate()
            deleteFilmRepository.deleteFilmInfo(updateDate).collectLatest { remoteResult ->
                when (remoteResult) {
                    is Result.Uninitialized -> {}
                    is Result.Success -> {
                        val dailyFilmItem = remoteResult.data ?: return@collectLatest
                        deleteFilmRepository.deleteVideo(
                            updateDate,
                            dailyFilmItem.videoUrl.toUri()
                        )
                            .collectLatest { result ->
                                when (result) {
                                    is Result.Uninitialized -> {}
                                    is Result.Success -> {
                                        uploadStorage()
                                    }
                                    is Result.Error -> {}
                                }
                            }
                    }
                    is Result.Error -> {}
                }
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

    companion object {
        const val KEY_INFO_ITEM = "beforeItem"
    }
}
