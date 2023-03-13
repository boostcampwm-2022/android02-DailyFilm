package com.boostcamp.dailyfilm.presentation.totalfilm

import android.net.Uri
import androidx.lifecycle.*
import com.boostcamp.dailyfilm.data.dataStore.UserPreferencesRepository
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.playfilm.PlayFilmRepository
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.model.SpeedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

@HiltViewModel
class TotalFilmViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val playFilmRepository: PlayFilmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val filmArray = savedStateHandle.get<ArrayList<DateModel>>(CalendarActivity.KEY_FILM_ARRAY)
    private val speedIndex = savedStateHandle.get<Int>(CalendarActivity.KEY_SPEED)

    private val _currentDateItem = MutableStateFlow(filmArray?.get(0))
    val currentDateItem: StateFlow<DateModel?> = _currentDateItem.asStateFlow()

    private val _downloadedVideoUri = MutableSharedFlow<Uri?>()
    val downloadedVideoUri: SharedFlow<Uri?> = _downloadedVideoUri.asSharedFlow()

    private val _isContentShowed = MutableLiveData(true)
    val isContentShowed: LiveData<Boolean> get() = _isContentShowed

    private val _isMuted = MutableLiveData(false)
    val isMuted: LiveData<Boolean> get() = _isMuted

    private val _isEnded = MutableStateFlow(false)
    val isEnded: StateFlow<Boolean> get() = _isEnded.asStateFlow()

    private val _isSpeed = MutableLiveData(SpeedState.values()[speedIndex ?: 2])
    val isSpeed: LiveData<SpeedState> get() = _isSpeed

    fun setCurrentDateItem(dateModel: DateModel) {
        _currentDateItem.value = dateModel
    }

    init {
        loadVideos()
    }

    fun changeShowState() {
        _isContentShowed.value = _isContentShowed.value?.not()
    }

    fun changeMuteState() {
        _isMuted.value = _isMuted.value?.not()
    }

    fun changeEndState() {
        _isEnded.value = _isEnded.value.not()
    }

    fun changeSpeedState() {
        _isSpeed.value = when (isSpeed.value) {
            SpeedState.NORMAL -> SpeedState.FAST_1_5
            SpeedState.FAST_1_5 -> SpeedState.FAST_2
            else -> SpeedState.NORMAL
        }
        viewModelScope.launch {
            preferencesRepository.editFast(isSpeed.value?.ordinal ?: 2)
        }
    }

    private fun loadVideos() {
        viewModelScope.launch {
            filmArray?.forEach { dateModel ->
                yield()
                val updateDate = dateModel.getDate()
                launch {
                    playFilmRepository.checkVideo(updateDate).collectLatest { localResult ->
                        when (localResult) {
                            is Result.Success -> {
                                if (localResult.data != null) {
                                    _downloadedVideoUri.emit(localResult.data)
                                } else {
                                    playFilmRepository.downloadVideo(updateDate)
                                        .collectLatest { remoteResult ->
                                            when (remoteResult) {
                                                is Result.Success -> {
                                                    val localUri = remoteResult.data
                                                    _downloadedVideoUri.emit(localUri)
                                                    playFilmRepository.insertVideo(
                                                        updateDate,
                                                        localUri.toString()
                                                    )
                                                        .collectLatest { insertResult ->
                                                            when (insertResult) {
                                                                is Result.Success -> {}
                                                                is Result.Error -> {}
                                                            }
                                                        }
                                                }
                                                is Result.Error -> {}
                                            }
                                        }
                                }
                            }
                            is Result.Error -> {}
                        }
                    }
                }
            }
            _downloadedVideoUri.emit(Uri.EMPTY)
        }
    }
}
