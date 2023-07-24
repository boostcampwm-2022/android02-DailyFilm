package com.boostcamp.dailyfilm.presentation.playfilm.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.airbnb.lottie.compose.LottieClipSpec

abstract class LottieState(initial: Boolean) {
    var state by mutableStateOf(initial)

    abstract val clipSpec: LottieClipSpec

    fun updateState() {
        state = !state
    }
}