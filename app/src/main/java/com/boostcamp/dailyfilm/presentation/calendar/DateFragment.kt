package com.boostcamp.dailyfilm.presentation.calendar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentDateBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_FILM_ARRAY
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmActivity
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmFragment.Companion.KEY_DATE_MODEL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DateFragment : BaseFragment<FragmentDateBinding>(R.layout.fragment_date) {

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
                reloadItem(calendarIndex, dateModel)
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        initSync()
        initBinding()
        collectFlow()
    }

    private fun collectFlow() {
        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.itemFlow.collect { item ->
                            viewModel.reloadCalendar(item)
                        }
                    }
                    launch {
                        viewModel.reloadFlow.collect {
                            reloadItem(it.first, it.second)
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.dateFlow.collectLatest { dateList ->
                        activityViewModel.emitFilm(
                            dateList.filter { dateModel -> dateModel.videoUrl != null }
                        )
                    }
                }
            }
        }
    }

    private fun reloadItem(index: Int, dateModel: DateModel) {
        binding.customCalendarView.reloadItem(
            index,
            dateModel
        ) { dateModel ->
            startForResult.launch(
                Intent(requireContext(), PlayFilmActivity::class.java).apply {
                    putExtra(
                        KEY_CALENDAR_INDEX,
                        index
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
    }

    private fun initBinding() {
        binding.fragment = this
        binding.activityViewModel = activityViewModel
        binding.viewModel = viewModel
    }

    private fun initSync() {
        if (viewModel.isSynced(viewModel.calendar.get(Calendar.YEAR)).not()) {
            viewModel.syncFilmItem()
        }
    }

    override fun onPause() {
        binding.customCalendarView.resetBackground()
        activityViewModel.changeSelectedItem(null, null)
        super.onPause()
    }

    companion object {
        const val KEY_CALENDAR = "calendar"
        const val KEY_DATE_MODEL_INDEX = "dateModelIndex"
        const val KEY_CALENDAR_INDEX = "calendarIndex"
        fun newInstance(calendar: Calendar): DateFragment {
            return DateFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_CALENDAR, calendar)
                }
            }
        }
    }
}
