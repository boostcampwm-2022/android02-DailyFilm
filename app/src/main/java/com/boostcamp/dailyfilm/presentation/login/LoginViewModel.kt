package com.boostcamp.dailyfilm.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.login.LoginRepository
import com.boostcamp.dailyfilm.data.login.LoginRepositoryImpl
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    var userInfo = MutableSharedFlow<FirebaseUser?>()

    fun requestLogin(idToken: String) {
        loginRepository.requestLogin(idToken).onEach { userInfo.emit(it) }.launchIn(viewModelScope)
    }
}