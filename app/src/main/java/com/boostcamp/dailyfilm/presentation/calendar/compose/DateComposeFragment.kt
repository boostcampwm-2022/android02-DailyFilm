package com.boostcamp.dailyfilm.presentation.calendar.compose

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentDateComposeBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_FILM_ARRAY
import com.boostcamp.dailyfilm.presentation.calendar.CalendarViewModel
import com.boostcamp.dailyfilm.presentation.calendar.DateViewModel
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmActivity
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmFragment.Companion.KEY_DATE_MODEL
import com.boostcamp.dailyfilm.presentation.ui.theme.DailyFilmTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DateComposeFragment :
    BaseFragment<FragmentDateComposeBinding>(R.layout.fragment_date_compose) {

    private val viewModel: DateViewModel by viewModels()
    private val activityViewModel: CalendarViewModel by activityViewModels()

    private val startForResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val calendarIndex = result.data?.getIntExtra(KEY_CALENDAR_INDEX, -1)
                    ?: return@registerForActivityResult
                val dateModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(KEY_DATE_MODEL, DateModel::class.java)
                } else {
                    result.data?.getParcelableExtra(KEY_DATE_MODEL)
                }
                dateModel ?: return@registerForActivityResult
                viewModel.setVideo(calendarIndex, dateModel)
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        initSync()
        initBinding()
    }

    private fun initBinding() {
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DailyFilmTheme(
                    requireActivity()
                ) {
                    CalendarView(
                        viewModel = viewModel,
                        resetFilm = {
                            activityViewModel.emitFilm(it)
                        },
                        imgClick = { idx, dateModel ->
                            Log.d("CalendarView", "${ArrayList(activityViewModel.filmFlow.value)}")
                            startForResult.launch(
                                Intent(requireContext(), PlayFilmActivity::class.java).apply {
                                    putExtra(
                                        KEY_CALENDAR_INDEX,
                                        idx
                                    )
                                    putExtra(
                                        KEY_DATE_MODEL_INDEX,
                                        activityViewModel.filmFlow.value.indexOf(dateModel)
                                    )
                                    putParcelableArrayListExtra(
                                        KEY_FILM_ARRAY,
                                        ArrayList(activityViewModel.filmFlow.value)
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    private fun initSync() {
        if (viewModel.isSynced(viewModel.calendar.get(Calendar.YEAR)).not()) {
            viewModel.syncFilmItem()
        }
    }

    companion object {
        const val KEY_CALENDAR = "calendar"
        const val KEY_DATE_MODEL_INDEX = "dateModelIndex"
        const val KEY_CALENDAR_INDEX = "calendarIndex"
        fun newInstance(calendar: Calendar): DateComposeFragment {
            return DateComposeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_CALENDAR, calendar)
                }
            }
        }
    }
}
