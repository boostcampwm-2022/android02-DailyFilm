package com.boostcamp.dailyfilm.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.login.LoginRepository
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.presentation.util.Event
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Event<UiState<FirebaseUser?>>>(Event(UiState.Uninitialized))
    val uiState = _uiState.asStateFlow()

    fun requestLogin(idToken: String) {
        event(UiState.Loading)
        loginRepository.requestLogin(idToken).onEach { result ->
            when (result) {
                is Result.Success -> {
                    event(UiState.Success(result.data))
                }
                is Result.Error -> {
                    event(UiState.Failure(result.exception))
                }
            }
        }.launchIn(viewModelScope)
    }
    private fun event(event: UiState<FirebaseUser?>) {
        viewModelScope.launch {
            _uiState.emit(Event(event))
        }
    }
}