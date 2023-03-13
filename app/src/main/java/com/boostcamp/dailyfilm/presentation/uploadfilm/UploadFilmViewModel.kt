package com.boostcamp.dailyfilm.presentation.uploadfilm


import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import androidx.lifecycle.*
import com.arthenica.mobileffmpeg.Config
import com.boostcamp.dailyfilm.data.delete.DeleteFilmRepository
import com.boostcamp.dailyfilm.data.model.DailyFilmItem
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.uploadfilm.UploadFilmRepository
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_EDIT_STATE
import com.boostcamp.dailyfilm.presentation.calendar.DateFragment.Companion.KEY_CALENDAR_INDEX
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmFragment.Companion.KEY_DATE_MODEL
import com.boostcamp.dailyfilm.presentation.playfilm.model.EditState
import com.boostcamp.dailyfilm.presentation.selectvideo.SelectVideoActivity.Companion.DATE_VIDEO_ITEM
import com.boostcamp.dailyfilm.presentation.uploadfilm.model.DateAndVideoModel
import com.boostcamp.dailyfilm.presentation.util.RoundedBackgroundSpan
import com.boostcamp.dailyfilm.presentation.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val startTime = savedStateHandle.get<Long>(KEY_START_TIME) ?: 0L
    val dateModel = savedStateHandle.get<DateModel>(KEY_DATE_MODEL)
    val calendarIndex = savedStateHandle.get<Int>(KEY_CALENDAR_INDEX)
    val editState = savedStateHandle.get<EditState>(KEY_EDIT_STATE)

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

    private val _compressProgress = MutableLiveData(0)
    val compressProgress: LiveData<Int> get() = _compressProgress

    init {
        calcProgress()
    }

    private fun calcProgress() {
        Config.resetStatistics()
        Config.enableStatisticsCallback {
            val percentage = it.videoFrameNumber
            _compressProgress.postValue(percentage)

            if (isEnded())
                _compressProgress.postValue(240)
        }
    }

    // 압축이 끝난 시점 -> ffmpeg log의 마지막 줄이 "video:...kB audio:...kB ..." 형태일 때
    private fun isEnded() =
        Config.getLastCommandOutput()
            .split("\n").dropLast(1).last()
            .substring(0 until 5) == "video"


    fun uploadVideo() {
        val text = textContent.value ?: ""
        val progress = _compressProgress.value ?: 0

        when {
            text.isEmpty() -> {
                _uiState.value = UiState.Failure(Throwable("영상에 맞는 문구를 입력해주세요."))
                return
            }
            (editState != EditState.EDIT_CONTENT) && progress < 240 -> {
                _uiState.value = UiState.Failure(Throwable("영상 처리중입니다. 잠시만 기다려주세요."))
                return
            }
        }

        editState?.let { state ->
            when (state) {
                EditState.NEW_UPLOAD -> uploadStorage()
                EditState.EDIT_CONTENT -> uploadEdit()
                EditState.RE_UPLOAD -> deleteVideo()
            }
        }
    }

    private fun uploadEdit() {

        val text = textContent.value ?: ""

        infoItem?.let { item ->
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                dateModel ?: return@launch
                val date = item.uploadDate
                val dailyFilmItem = DailyFilmItem(dateModel.videoUrl.toString(), text, date)
                when (val result = uploadFilmRepository.uploadEditVideo(date, dailyFilmItem)) {
                    is Result.Success -> {
                        _uiState.value = UiState.Success(
                            dateModel.copy(text = text)
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = UiState.Failure(result.exception)
                    }
                }
            }
        }
    }

    private fun uploadStorage() {
        infoItem?.let { item ->
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                when (val result = uploadFilmRepository.uploadVideo(item.uploadDate, item.uri)) {
                    is Result.Success -> {
                        uploadRealtime(result.data)
                    }
                    is Result.Error -> {
                        _uiState.value = UiState.Failure(result.exception)
                    }
                }
            }
        }
    }

    private fun uploadRealtime(videoUrl: Uri?) {
        val uploadDate = infoItem?.uploadDate
        val text = textContent.value ?: ""
        if (dateModel ==null){
            _uiState.value = UiState.Failure(Throwable("dateModel Fail"))
            return
        }
        if (videoUrl != null && uploadDate != null) {
            val filmItem = DailyFilmItem(videoUrl.toString(), text, uploadDate)
            viewModelScope.launch {
                when (val result = uploadFilmRepository.uploadFilmInfo(uploadDate, filmItem)) {
                    is Result.Success -> {
                        uploadFilmRepository.insertFilmEntity(filmItem)
                        _uiState.value = UiState.Success(
                            dateModel.copy(text = text, videoUrl = videoUrl.toString())
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = UiState.Failure(result.exception)
                    }
                }
            }
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

            when (val result = deleteFilmRepository.delete(updateDate)) {
                is Result.Success -> {
                    uploadStorage()
                }
                is Result.Error -> {
                    UiState.Failure(result.exception)
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
        const val KEY_START_TIME = "start_time"
    }
}
