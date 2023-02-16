package com.boostcamp.dailyfilm.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.login.LoginRepository
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.presentation.util.UiState
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<FirebaseUser?>>(UiState.Uninitialized)
    val uiState = _uiState.asStateFlow()

    fun requestLogin(idToken: String) {
        _uiState.value = UiState.Loading
        loginRepository.requestLogin(idToken).onEach { result ->
            when (result) {
                is Result.Success -> {
                    _uiState.value = UiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = UiState.Failure(result.exception)
                }
            }
        }.launchIn(viewModelScope)
    }
}