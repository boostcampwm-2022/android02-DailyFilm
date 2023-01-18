package com.boostcamp.dailyfilm.presentation.playfilm

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.*
import com.boostcamp.dailyfilm.data.delete.DeleteFilmRepository
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.playfilm.PlayFilmRepository
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayFilmViewModel @Inject constructor(
    private val playFilmRepository: PlayFilmRepository,
    private val deleteFilmRepository: DeleteFilmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val dateModel = savedStateHandle.get<DateModel>(PlayFilmFragment.KEY_DATE_MODEL)
        ?: throw IllegalStateException("PlayFilmViewModel - DateModel is null")

    private val _videoUri = MutableLiveData<Uri?>()
    val videoUri: LiveData<Uri?> get() = _videoUri

    private val _isContentShowed = MutableLiveData(true)
    val isContentShowed: LiveData<Boolean> get() = _isContentShowed

    private val _isMuted = MutableLiveData(false)
    val isMuted: LiveData<Boolean> get() = _isMuted

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Uninitialized)
    val uiState = _uiState.asStateFlow()

    init {
        loadVideo()
    }

    fun changeShowState() {
        _isContentShowed.value = _isContentShowed.value?.not()
    }

    fun changeMuteState() {
        _isMuted.value = _isMuted.value?.not()
    }

    private fun loadVideo() {
        viewModelScope.launch {
            val updateDate = dateModel.getDate()
            playFilmRepository.checkVideo(updateDate).collectLatest { localResult ->
                when (localResult) {
                    is Result.Uninitialized -> return@collectLatest
                    is Result.Success -> {
                        if (localResult.data != null) {
                            Log.d("LoadVideo", "Cached ${localResult.data}")
                            _videoUri.value = localResult.data
                        } else { // 다운로드 해야됨
                            playFilmRepository.downloadVideo(updateDate)
                                .collectLatest { remoteResult ->
                                    when (remoteResult) {
                                        is Result.Uninitialized -> {}
                                        is Result.Success -> {
                                            val localUri = remoteResult.data
                                            _videoUri.value = localUri
                                            playFilmRepository.insertVideo(
                                                updateDate,
                                                localUri.toString()
                                            ).collectLatest { insertResult ->
                                                when (insertResult) {
                                                    is Result.Uninitialized -> {}
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

    fun deleteVideo() {
        Log.d("deleteVideo", "start")
        viewModelScope.launch {
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
                                        _uiState.value = UiState.Success(Unit)
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

}