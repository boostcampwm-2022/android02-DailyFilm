package com.boostcamp.dailyfilm.presentation.calendar

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.dailyfilm.R
import com.boostcamp.dailyfilm.databinding.FragmentDateBinding
import com.boostcamp.dailyfilm.presentation.BaseFragment
import com.boostcamp.dailyfilm.presentation.calendar.adpater.CalendarAdapter
import com.boostcamp.dailyfilm.presentation.calendar.model.DateModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class DateFragment(onUploadFilm: (DateModel) -> Unit) :
    BaseFragment<FragmentDateBinding>(R.layout.fragment_date) {

    private val viewModel: DateViewModel by viewModels()
    private val adapter = CalendarAdapter(
        { dateModel ->
            Toast.makeText(requireContext(), "img", Toast.LENGTH_SHORT).show()
        },
        { dateModel ->
            onUploadFilm(dateModel)
            Toast.makeText(requireContext(), "$dateModel", Toast.LENGTH_SHORT).show()
        })

    override fun initView() {

        initAdapter()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dateFlow.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun initAdapter() {
        binding.rvCalendar.adapter = adapter
    }

    companion object {
        const val KEY_CALENDAR = "calendar"
        fun newInstance(calendar: Calendar, lambda: (DateModel) -> Unit): DateFragment {
            return DateFragment(lambda).apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_CALENDAR, calendar)
                }
            }
        }
    }
}
