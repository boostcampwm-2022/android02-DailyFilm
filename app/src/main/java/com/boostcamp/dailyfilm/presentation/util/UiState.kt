package com.boostcamp.dailyfilm.presentation.util


sealed class UiState<out T> {
    object Uninitialized : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val item: T) : UiState<T>()
    data class Failure(val throwable: Throwable) : UiState<Nothing>()
}
