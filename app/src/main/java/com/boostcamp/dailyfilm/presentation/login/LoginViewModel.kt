package com.boostcamp.dailyfilm.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.repository.login.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var userInfo = MutableSharedFlow<FirebaseUser?>()

    fun requestLogin(idToken: String) {
        authRepository.requestLogin(idToken).onEach { userInfo.emit(it) }.launchIn(viewModelScope)
    }
}