package com.boostcamp.dailyfilm.presentation.calendar

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentCalendarBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()

    override fun initView() {
        binding.viewModel = viewModel

        viewModel.testCalendar()
    }

    companion object {
        const val KEY_CALENDAR = "calendar"
        fun newInstance(calendar: Calendar): CalendarFragment {
            return CalendarFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_CALENDAR, calendar)
                }
            }
        }
    }
}
