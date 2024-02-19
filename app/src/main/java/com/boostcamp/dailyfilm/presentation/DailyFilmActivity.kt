package com.boostcamp.dailyfilm.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.boostcamp.dailyfilm.presentation.calendar.DateComposeViewModel
import com.boostcamp.dailyfilm.presentation.totalfilm.ui.theme.DailyFilmTheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@AndroidEntryPoint
class DailyFilmActivity : ComponentActivity() {

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ViewModelFactoryProvider {
        fun provideDateViewModelFactory(): DateComposeViewModel.Factory
    }

    @Inject
    lateinit var dateViewModelFactory: DateComposeViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DailyFilmTheme {
                com.boostcamp.dailyfilm.presentation.ui.theme.DailyFilmTheme {
                    DailyFilmApp()
                }
            }
        }
    }
}
