package com.boostcamp.dailyfilm.presentation.calendar.compose

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color


class DateState {
    var isCurrentMonth: Boolean = true
    var selectedDay by mutableStateOf<Int?>(null)

    val alpha: Float get() = if (isCurrentMonth) 0.3f else 1f

    @Composable
    fun isSelected(idx: Int) =
        if (selectedDay == idx) Color.Gray else MaterialTheme.colors.background
}