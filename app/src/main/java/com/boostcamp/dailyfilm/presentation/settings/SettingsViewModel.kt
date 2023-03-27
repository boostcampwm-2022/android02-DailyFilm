package com.boostcamp.dailyfilm.presentation.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.settings.SettingsRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.boostcamp.dailyfilm.data.model.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _settingsEventFlow = MutableSharedFlow<SettingsEvent>()
    val settingsEventFlow: SharedFlow<SettingsEvent> = _settingsEventFlow.asSharedFlow()

    fun backToPrevious() = event(SettingsEvent.Back)

    fun logout() {
        event(SettingsEvent.Logout)

        viewModelScope.launch {
            settingsRepository.deleteAllData().collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        event(SettingsEvent.Logout)
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteUser() {

        FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                viewModelScope.launch {
                    settingsRepository.deleteAllData().collectLatest { result ->
                        when (result) {
                            is Result.Success -> event(SettingsEvent.DeleteUser)
                            else -> {}
                        }
                    }
                }
            }
        }

    }

    private fun event(settingsEvent: SettingsEvent) =
        viewModelScope.launch { _settingsEventFlow.emit(settingsEvent) }


}

sealed class SettingsEvent {

    object Back : SettingsEvent()
    object Logout : SettingsEvent()
    object DeleteUser : SettingsEvent()
}
