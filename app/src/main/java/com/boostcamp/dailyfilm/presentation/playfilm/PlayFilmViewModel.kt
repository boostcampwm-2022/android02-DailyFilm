package com.boostcamp.dailyfilm.presentation.playfilm

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.delete.DeleteFilmRepository
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.playfilm.PlayFilmRepository
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.base.ContentShowState
import com.boostcamp.dailyfilm.presentation.playfilm.base.MuteState
import com.boostcamp.dailyfilm.presentation.util.PlayState
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.boostcamp.dailyfilm.presentation.util.network.NetworkManager
import com.boostcamp.dailyfilm.presentation.util.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayFilmViewModel @Inject constructor(
    private val playFilmRepository: PlayFilmRepository,
    private val deleteFilmRepository: DeleteFilmRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var dateModel = savedStateHandle.get<DateModel>(PlayFilmFragment.KEY_DATE_MODEL)
        ?: throw IllegalStateException("PlayFilmViewModel - DateModel is null")

    private val _text = MutableLiveData<String>(dateModel.text)
    val text: LiveData<String> get() = _text

    private val _videoUri = MutableLiveData<Uri?>()
    val videoUri: LiveData<Uri?> get() = _videoUri

    private val _contentShowState = MutableStateFlow(ContentShowState(true))
    val contentShowState: StateFlow<ContentShowState> get() = _contentShowState

    private val _muteState = MutableStateFlow(MuteState(false))
    val muteState: StateFlow<MuteState> get() = _muteState

    private val _playState = MutableStateFlow<PlayState>(PlayState.Uninitialized)
    val playState: StateFlow<PlayState> get() = _playState

    private val _networkState = MutableLiveData(NetworkManager.checkNetwork())
    val networkState: LiveData<NetworkState> get() = _networkState

    private val _isNetworkConnectShowed = MutableLiveData(true)
    val isNetworkConnectShowed: LiveData<Boolean> get() = _isNetworkConnectShowed

    private val _isProgressed = MutableStateFlow(false)
    val isProgressed: StateFlow<Boolean> get() = _isProgressed

    private val _openDialog = MutableStateFlow(false)
    val openDialog : StateFlow<Boolean> get() = _openDialog

    init {
        loadVideo()
    }
    fun openDialog() {
        _openDialog.value = true
    }
    fun closeDialog() {
        _openDialog.value = false
    }

    private fun checkNetwork() {
        _networkState.value = NetworkManager.checkNetwork()
    }

    fun setDateModel(text: String) {
        dateModel = dateModel.copy(text = text)
        _text.value = text
    }

    fun setNetworkState(state: NetworkState) {
        viewModelScope.launch {
            // isNetworkConnected 는 연결 여부를 떠나 Playing 중이면 보여 주지 않는다.
            _networkState.value = state
            _isNetworkConnectShowed.value = _playState.value != PlayState.Playing && !state.value
        }
    }

    /**
     *  local uri 확인
     *   - uri 가 존재 하지 않다면 서버에서 가져 오기
     */
    private fun loadVideo() {
        viewModelScope.launch {
            _playState.value = PlayState.Loading

            playFilmRepository.checkVideo(dateModel.getDate()).collectLatest { localResult ->
                when (localResult) {
                    is Result.Success -> {
                        if (localResult.data != null) {
                            Log.d(
                                "LoadVideo",
                                "date: ${dateModel.day} localResult.data: ${localResult.data} "
                            )
                            _videoUri.value = localResult.data
                            _playState.value = PlayState.Playing
                        } else {
                            checkNetwork()
                            downloadVideo()
                        }
                    }
                    is Result.Error -> {
                        checkNetwork()
                    }
                }
            }
        }
    }

    private fun downloadVideo() {
        viewModelScope.launch {
            playFilmRepository.downloadVideo(dateModel.getDate())
                .collectLatest { remoteResult ->
                    when (remoteResult) {
                        is Result.Success -> {
                            val localUri = remoteResult.data
                            _videoUri.value = localUri
                            cacheVideo(localUri.toString())
                        }
                        is Result.Error -> {
                            checkNetwork()
                        }
                    }
                }
        }
    }

    private fun cacheVideo(uri: String) {
        viewModelScope.launch {
            playFilmRepository.insertVideo(
                dateModel.getDate(),
                uri
            ).collectLatest { insertResult ->
                when (insertResult) {
                    is Result.Success -> {
                        _playState.value = PlayState.Playing
                    }
                    is Result.Error -> {
                        checkNetwork()
                    }
                }
            }
        }
    }

    fun deleteVideo() {
        viewModelScope.launch {
            when (val result = deleteFilmRepository.delete(dateModel.getDate())) {
                is Result.Success -> {
                    _playState.value = PlayState.Deleted(
                        DateModel(
                            year = dateModel.year,
                            month = dateModel.month,
                            day = dateModel.day
                        )
                    )
                }
                is Result.Error -> {
                    UiState.Failure(result.exception)
                }
            }
        }
    }

}