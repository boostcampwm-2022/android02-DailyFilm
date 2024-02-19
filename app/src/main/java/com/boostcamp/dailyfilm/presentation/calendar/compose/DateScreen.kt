package com.boostcamp.dailyfilm.presentation.calendar.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.boostcamp.dailyfilm.presentation.calendar.DateViewModel
import java.util.Calendar

@Composable
fun DateScreen(
    calendar: Calendar,
    // viewModel: DateViewModel = hiltViewModel(),
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월")
    }
}
