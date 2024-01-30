package com.boostcamp.dailyfilm.presentation.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.boostcamp.dailyfilm.presentation.calendar.compose.MainView
import com.boostcamp.dailyfilm.presentation.ui.theme.DailyFilmTheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@AndroidEntryPoint
class MainTestActivity : ComponentActivity() {

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
                MainView()
            }
        }
    }
}
