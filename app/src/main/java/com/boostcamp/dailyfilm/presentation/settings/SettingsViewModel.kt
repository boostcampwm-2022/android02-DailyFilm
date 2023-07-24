package com.boostcamp.dailyfilm.presentation.settings

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.boostcamp.dailyfilm.data.model.Result
import com.boostcamp.dailyfilm.data.settings.SettingsRepository
import com.boostcamp.dailyfilm.data.sync.SyncRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {
    private val _settingsEventFlow = MutableStateFlow<SettingsEvent>(SettingsEvent.Initialized)
    val settingsEventFlow: StateFlow<SettingsEvent> = _settingsEventFlow.asStateFlow()

    private val _openDialog = MutableStateFlow(DialogState(false, "", {}))
    val openDialog : StateFlow<DialogState> get() = _openDialog

    fun closeDialog() {
        _openDialog.value = _openDialog.value.copy(openDialog = false)
    }
    fun openDialog(content: String, confirm: () -> Unit) {
        _openDialog.value = _openDialog.value.copy(content = content, confirm = confirm)
    }

    fun backToPrevious() = event(SettingsEvent.Back)

    fun logout() {
        Log.d("Logout", "SettingsViewModel")
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
        viewModelScope.launch { _settingsEventFlow.value = settingsEvent }
}

data class DialogState(
    val openDialog: Boolean,
    val content: String,
    val confirm: () -> Unit,
)

sealed class SettingsEvent {
    object Initialized : SettingsEvent()
    object Back : SettingsEvent()
    object Logout : SettingsEvent()
    object DeleteUser : SettingsEvent()
}
