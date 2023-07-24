package com.boostcamp.dailyfilm.presentation.playfilm.base

import com.airbnb.lottie.compose.LottieClipSpec

class ContentShowState(init: Boolean): LottieState(init) {
    override val clipSpec: LottieClipSpec
        get() = if (state) {
            LottieClipSpec.Progress(START, MID)
        } else {
            LottieClipSpec.Progress(MID, FINISH)
        }

    companion object {
        const val START = 0.67f
        const val MID = 0.25f
        const val FINISH = 0.67f
    }
}