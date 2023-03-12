package com.boostcamp.dailyfilm.presentation.playfilm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SpeedState(val speed: Float) : Parcelable {
    NORMAL(1f),
    FAST_1_5(1.5f),
    FAST_2(2f);

    override fun toString(): String {
        return when (this) {
            NORMAL -> "x1.0"
            FAST_1_5 -> "x1.5"
            FAST_2 -> "x2.0"
        }
    }
}