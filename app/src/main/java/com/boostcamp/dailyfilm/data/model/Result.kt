package com.boostcamp.dailyfilm.data.model

import java.io.IOException


sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>() {
        val isNetworkError = exception is IOException
    }
}