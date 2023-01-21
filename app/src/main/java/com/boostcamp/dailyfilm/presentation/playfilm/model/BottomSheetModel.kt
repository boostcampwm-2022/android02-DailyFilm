package com.boostcamp.dailyfilm.presentation.playfilm.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class BottomSheetModel(
    @DrawableRes val icon: Int,
    @StringRes val title: Int
)
