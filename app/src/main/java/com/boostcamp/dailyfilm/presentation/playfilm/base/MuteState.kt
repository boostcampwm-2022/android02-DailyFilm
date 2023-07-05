package com.boostcamp.dailyfilm.presentation.playfilm.base

import com.airbnb.lottie.compose.LottieClipSpec

class MuteState(init: Boolean): LottieState(init) {

    override val clipSpec: LottieClipSpec
        get() = if (state) {
            LottieClipSpec.Progress(START, MID)
        } else {
            LottieClipSpec.Progress(MID, FINISH)
        }

    companion object {
        const val START = 0.0f
        const val MID = 0.5f
        const val FINISH = 1.0f
    }
}