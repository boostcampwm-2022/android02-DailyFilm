package com.boostcamp.dailyfilm.presentation.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.settings.SettingsRepository
import com.boostcamp.dailyfilm.data.sync.SyncRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository,
    private val syncRepository: SyncRepository
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
                        syncRepository.clearSyncedYear()
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
                            is Result.Success -> {
                                syncRepository.clearSyncedYear()
                                event(SettingsEvent.DeleteUser)
                            }
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
