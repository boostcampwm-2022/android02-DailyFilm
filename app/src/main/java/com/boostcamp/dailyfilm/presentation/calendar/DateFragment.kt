package com.boostcamp.dailyfilm.presentation.calendar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentDateBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.CalendarActivity.Companion.KEY_FILM_ARRAY
import com.boostcamp.dailyfilm.presentation.playfilm.PlayFilmActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DateFragment : BaseFragment<FragmentDateBinding>(R.layout.fragment_date) {

    private val viewModel: DateViewModel by viewModels()
    private val activityViewModel: CalendarViewModel by activityViewModels()

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
                            binding.customCalendarView.reloadItem(
                                it.first,
                                it.second
                            ) { dateModel ->
                                startActivity(
                                    Intent(requireContext(), PlayFilmActivity::class.java).apply {
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

    private fun initBinding() {
        binding.fragment = this
        binding.activityViewModel = activityViewModel
        binding.viewModel = viewModel
    }

    private fun initSync() {
        if (activityViewModel.syncSet.contains(viewModel.calendar.get(Calendar.YEAR)).not()) {
            viewModel.syncFilmItem()
            activityViewModel.syncSet.add(viewModel.calendar.get(Calendar.YEAR))
        }
    }

    override fun onPause() {
        binding.customCalendarView.resetBackground()
        activityViewModel.changeSelectedItem(null)
        super.onPause()
    }

    companion object {
        const val KEY_CALENDAR = "calendar"
        const val KEY_DATE_MODEL_INDEX = "dateModelIndex"
        fun newInstance(calendar: Calendar): DateFragment {
            return DateFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_CALENDAR, calendar)
                }
            }
        }
    }
}
