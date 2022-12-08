package com.boostcamp.dailyfilm.presentation.calendar

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentCalendarCustomBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DateFragment(val onUploadFilm: (DateModel?) -> Unit) :
    BaseFragment<FragmentCalendarCustomBinding>(R.layout.fragment_calendar_custom) {

    private val viewModel: DateViewModel by viewModels()
    private val activityViewModel: CalendarViewModel by activityViewModels()
    private lateinit var adapter: CalendarAdapter

    override fun initView() {

        if (activityViewModel.syncSet.contains(viewModel.calendar.get(Calendar.YEAR)).not()) {
            viewModel.syncFilmItem()
            activityViewModel.syncSet.add(viewModel.calendar.get(Calendar.YEAR))
        }

//        initAdapter()

        lifecycleScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        viewModel.dateFlow.collect { dateList ->
                            binding.customCalendarView.initCalendar(
                                Glide.with(this@DateFragment),
                                dateList
                            )
                            cancel()
                        }
                    }
                    launch {
                        viewModel.reloadFlow.collect {
                            binding.customCalendarView.reloadItem(it.first, it.second)
                        }
                    }
                    launch {
                        viewModel.itemFlow.collect { item ->
                            viewModel.reloadCalendar(item)
                        }
                    }
                }
            }

            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    launch {
                        viewModel.dateFlow.collectLatest { dateList ->
                            activityViewModel.emitFilm(
                                dateList.filter { dateModel -> dateModel.videoUrl != null }
                            )
                        }
                    }
                }
            }
        }
    }

    /*private fun initAdapter() {
        adapter = CalendarAdapter(viewModel.calendar, Glide.with(this), { dateModel ->
            onUploadFilm(null)
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
        }, { dateModel ->
            onUploadFilm(dateModel)
        })
        binding.rvCalendar.adapter = adapter
        binding.rvCalendar.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL).apply {
                ContextCompat.getDrawable(requireContext(), R.drawable.div_calendar_week)?.let {
                    setDrawable(it)
                }
            }
        )
    }*/

    companion object {
        const val KEY_CALENDAR = "calendar"
        const val KEY_DATE_MODEL_INDEX = "dateModelIndex"
        fun newInstance(calendar: Calendar, lambda: (DateModel?) -> Unit): DateFragment {
            return DateFragment(lambda).apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_CALENDAR, calendar)
                }
            }
        }
    }
}
