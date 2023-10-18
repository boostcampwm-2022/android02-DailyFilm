package com.boostcamp.dailyfilm.presentation.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.boostcamp.dailyfilm.presentation.calendar.compose.MainView
import com.boostcamp.dailyfilm.presentation.ui.theme.DailyFilmTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyFilmTheme {
                MainView()
            }
        }
    }
}
