package com.boostcamp.dailyfilm.presentation.playfilm.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.boostcamp.dailyfilm.R
import kotlinx.parcelize.Parcelize

@Parcelize
enum class SpeedState(val speed: Float, @DrawableRes val res: Int) : Parcelable {
    NORMAL(1f, R.drawable.ic_fast),
    FAST_1_5(1.5f, R.drawable.ic_fast),
    FAST_2(2f, R.drawable.ic_fast)
}